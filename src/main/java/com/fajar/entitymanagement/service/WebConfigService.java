package com.fajar.entitymanagement.service;

import static com.fajar.entitymanagement.util.CollectionUtil.isEmptyArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.UserRole;
import com.fajar.entitymanagement.repository.AppProfileRepository;
import com.fajar.entitymanagement.repository.MenuRepository;
import com.fajar.entitymanagement.repository.PageRepository;
import com.fajar.entitymanagement.repository.UserRepository;
import com.fajar.entitymanagement.repository.UserRoleRepository;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */

@Data
@Slf4j
public class WebConfigService {

	public static final String DEFAULT_ROLE = "00";
	public static final String SETTING = "setting";
	public static final String MENU = "menu";
	public static final String PAGE = "page";
	public static final String ADMIN = "admin";

	@Autowired
	private AppProfileRepository ProfileRepository;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private MenuRepository menuRepository;

	/////////////// VARIABLES REGISTERED VIA XML ////////////////
	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	private String reportPath;
	private String appCode;
	private String DEFAULT_USER_NAME;
	private String DEFAULT_USER_PWD;
	
	private Menu defaultMenuManagementMenu, defaultPageManagementMenu;
	private Page defaultSettingPage, defaultManagementPage, defaultAdminPage;
	private User defaultUser;
	private UserRole defaultUserRole;
	private Profile defaultProfile;
	/////////////////////////////////////////////////////////////

	private List<JpaRepository<?, ?>> jpaRepositories = new ArrayList<>();
	private List<Type> entityClassess = new ArrayList<>();
	private Map<Class<? extends BaseEntity>, JpaRepository> repositoryMap = new HashMap<>();
	
	
	@PostConstruct
	public void init() {
		log.info("WebConfigService INITIALIZE");
		
		LogProxyFactory.setLoggers(this);
		
		getJpaReporitoriesBean();
		checkUser();
		checkDefaultProfile();
		defaultAdminPage();
	}

	private void checkUser() {
		UserRole userRole = userRoleRepository.findByCode(DEFAULT_ROLE);
		if (null == userRole) {
			userRole = defaultRole();
		}
		User user = userRepository.findByUsername(DEFAULT_USER_NAME);
		if (null == user) {
			user = defaultUser();
		}
	}

	private User defaultUser() {
		User user = userRepository.findByUsername(DEFAULT_USER_NAME);
		if (null != user) {
			return user;
		}
		user = defaultUser;
		user.setRole(defaultRole());  
		
		return userRepository.save(user);
	}

	public UserRole defaultRole() {
		UserRole userRole = userRoleRepository.findByCode(DEFAULT_ROLE);
		if (null != userRole) {
			return userRole;
		}
		userRole = defaultUserRole;

		return userRoleRepository.save(userRole);
	}

	private void getJpaReporitoriesBean() {
		log.info("//////////////GET JPA REPOSITORIES BEANS///////////////");
		jpaRepositories.clear();
		entityClassess.clear();
		String[] beanNames = applicationContext.getBeanNamesForType(JpaRepository.class);
		if (null == beanNames)
			return;

		log.info("JPA REPOSITORIES COUNT: " + beanNames.length);
		for (int i = 0; i < beanNames.length; i++) {
			String beanName = beanNames[i];
			JpaRepository<?, ?> beanObject = (JpaRepository<?, ?>) applicationContext.getBean(beanName);

			if (null == beanObject)
				continue;
			Class<?>[] interfaces = beanObject.getClass().getInterfaces();

			log.info("beanObject: {}", beanObject);
			if (null == interfaces)
				continue;

			Type type = getTypeArgument(interfaces[0], 0);

			entityClassess.add(type);
			jpaRepositories.add(beanObject);

			repositoryMap.put((Class) type, beanObject);

			log.info(i + "." + beanName + ". entity type: " + type);
		}
	}

	public Profile getProfile() {
		return checkDefaultProfile();
	}
	
	public Menu getMenu(String code, Menu defaultMenuIfNotExist, Page menuPage) {
		Page eixsitingPage = getPage(menuPage.getCode(), menuPage);
		Menu menu = menuRepository.findByCode(code);
		if (null != menu) {
			log.info("menu: {} FOUND!", code);
			return menu;
		}

		log.info("WILL SAVE menu with :{}", code);

		menu = defaultMenuIfNotExist; 
		menu.setMenuPage(eixsitingPage); 

		return menuRepository.save(menu);
	}
	
	public Menu checkDefaultMenu() {
		return getMenu(MENU, defaultMenuManagementMenu, defaultSettingPage());
	}

	public Menu checkPageManagementMenu() {
		return getMenu(PAGE, defaultPageManagementMenu, defaultSettingPage());
	}
	
	private Page getPage(String code, Page defaultPageIfNotExist) {
		Page page = pageRepository.findByCode(code);
		if (null != page) {
			log.info("page with code: {} FOUND!", code);
			return page;
		}
		log.info("WILL SAVE page : {}...", code);
		return pageRepository.save(defaultPageIfNotExist);
	}
	 
	public Page defaultAdminPage() {
		return getPage(ADMIN, defaultAdminPage);
	}
	
	public Page defaultSettingPage() {
		
		return getPage(SETTING, defaultSettingPage);
		 
	}

	public static void main(String[] args) throws IOException {
//		Class _class = ProductRepository.class;
//		Type[] interfaces = _class.getGenericInterfaces();
//		CollectionUtil.printArray(interfaces);
//		Type type = interfaces[0];
//		System.out.println("type: "+type);
//		ParameterizedType parameterizedType = (ParameterizedType) type;
//		log.info("parameterizedType: {}", parameterizedType );
	}

	private ParameterizedType getJpaRepositoryType(Class<?> _class) {
		Type[] genericInterfaces = _class.getGenericInterfaces();
		if (CollectionUtil.isEmptyArray(genericInterfaces))
			return null;

		try {
			for (int i = 0; i < genericInterfaces.length; i++) {
				Type genericInterface = genericInterfaces[i];
				if (genericInterface.getTypeName()
						.startsWith("org.springframework.data.jpa.repository.JpaRepository")) {
					return (ParameterizedType) genericInterface;
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private Type getTypeArgument(Class<?> _class, int argNo) {
		try {

			ParameterizedType jpaRepositoryType = getJpaRepositoryType(_class);

			Type[] typeArguments = jpaRepositoryType.getActualTypeArguments();// type.getTypeParameters();
			CollectionUtil.printArray(typeArguments);

			if (isEmptyArray(typeArguments)) {
				return null;
			}

			Type typeArgument = typeArguments[argNo];
			log.debug("typeArgument: {}", typeArgument);
			return typeArgument;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String readFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			return everything;
		} finally {
			br.close();
		}
	}

	public Profile checkDefaultProfile() {
		Profile dbProfile = ProfileRepository.findByAppCode(appCode);
		if (null == dbProfile) {
			ProfileRepository.save(defaultProfile);
		}
		dbProfile = ProfileRepository.findByAppCode(appCode);

		/* return getProfileFromSession(); */ return EntityUtil.validateDefaultValue(dbProfile);
	}
 

	public <T extends BaseEntity> JpaRepository getJpaRepository(Class<T> _entityClass) {
		log.info("get JPA Repository for: {}", _entityClass);

		JpaRepository result = this.repositoryMap.get(_entityClass);

		log.info("found repo object: {}", result);

		return result;
	}

}
