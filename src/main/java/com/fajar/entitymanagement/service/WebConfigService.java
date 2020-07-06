package com.fajar.entitymanagement.service;

import static com.fajar.entitymanagement.util.CollectionUtil.isEmptyArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
//	private Menu defaultMenu;
//	private Page defaultMenuPage;
	/////////////////////////////////////////////////////////////
	

	private List<JpaRepository<?, ?>> jpaRepositories = new ArrayList<>();
	private List<Type> entityClassess = new ArrayList<>();
	private Map<Class<? extends BaseEntity>, JpaRepository> repositoryMap = new HashMap<>();

	

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		Profile dbProfile = ProfileRepository.findByAppCode(appCode);
		if (null == dbProfile) {
			ProfileRepository.save(defaultProfile());
		}
		getJpaReporitoriesBean();
		checkUser();
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
		user = new User();
		user.setUsername(DEFAULT_USER_NAME);
		user.setPassword(DEFAULT_USER_PWD);
		user.setRole(defaultRole());
		user.setDisplayName("DEFAULT USER");

		return userRepository.save(user);
	}

	public UserRole defaultRole() {
		UserRole userRole = userRoleRepository.findByCode(DEFAULT_ROLE);
		if (null != userRole) {
			return userRole;
		}
		userRole = new UserRole();
		userRole.setCode(DEFAULT_ROLE);
		userRole.setName("Default Role");
		userRole.setAccess(DEFAULT_ROLE);

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

	public Menu checkDefaultMenu() {
		checkPageManagementMenu();
		
		Menu menu = menuRepository.findByCode(MENU);
		if (null != menu) {
			log.info("defaultMenu FOUND!");
			return menu;
		}
		
		log.info("WILL SAVE defaultMenu...");

		menu = new Menu();
		menu.setCode(MENU);
		menu.setName("Menu Management");
		menu.setUrl("/management/common/menu");
		Page menuPage = defaultPage();
		menu.setMenuPage(menuPage);
		menu.setColor("#ffffff");
		menu.setFontColor("#000000");
		menu.setDescription("Default "+MENU+" Page");
		
		return menuRepository.save(menu);
	}
	
	public void checkPageManagementMenu() {
		Menu menu = menuRepository.findByCode(PAGE);
		if (null != menu) {
			log.info("Page Management Menu FOUND!");
			return;
		}
		
		log.info("WILL Add Page Management Menu...");

		menu = new Menu();
		menu.setCode(PAGE);
		menu.setName("Page Management");
		menu.setUrl("/management/common/page");
		Page menuPage = defaultPage();
		menu.setMenuPage(menuPage);
		menu.setColor("#ffffff");
		menu.setFontColor("#000000");
		menu.setDescription("Generated Page Management");
		
		menuRepository.save(menu);
	}

	private Page defaultPage() {
		Page menuPage = pageRepository.findByCode(SETTING);
		if (null != menuPage) {
			log.info("defaultPage FOUND!");
			return menuPage;
		}

		log.info("WILL SAVE defaultPage...");
		
		menuPage = new Page();
		menuPage.setCode(SETTING);
		menuPage.setCreatedDate(new Date());
		menuPage.setDescription("Default Setting Page");
		menuPage.setLink("/webpage/page/"+SETTING);
		menuPage.setName(SETTING);
		menuPage.setNonMenuPage(0);
		menuPage.setAuthorized(1);
		menuPage.setSequence(0);

		return pageRepository.save(menuPage);
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

	public static void main2(String[] args) throws IOException {
		String path = "C:\\Users\\Republic Of Gamers\\Documents\\ORCALE_DUMPS\\";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				// System.out.println(path+file.getName());
				String content = readFile(path + file.getName());

				if (!file.getName().startsWith("MASTER_PFM_DETAIL")) {
					continue;
				}
				filter(content);
			} else if (file.isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}

	public static String filter(String content) {
		String[] lines = (content.split("\n"));
		String result = "";
		String firstLine = lines[0].trim();
		if (firstLine.startsWith("INSERT") == false) {
			firstLine = firstLine.substring(3, firstLine.length());
			firstLine = firstLine.replace("MMB", "DEVELOPMENT");
		}

		for (String string : lines) {
			if (string.trim().equals(";")) {
				continue;
			}

			String insertStatement = string.substring(3, string.length());
			if (insertStatement.startsWith("INSERT")) {
				string = string.substring(3, string.length());
			}

			string = string.trim();
			if (string.startsWith(",(")) {
				string = string.substring(0, string.length());
				string = firstLine + string;
			}
			if (string.endsWith(")")) {
				string += ";";
			}
			string = string.replace("MMB", "DEVELOPMENT");
			string = string.replace("VALUES,", "VALUES");
			result += "\n" + string;

			System.out.println(string);
		}
		return result;
	}

	public Profile getProfile() {
		Profile dbProfile = ProfileRepository.findByAppCode(appCode);

		/* return getProfileFromSession(); */ return EntityUtil.validateDefaultValue(dbProfile);
	}

	private Profile defaultProfile() {
		Profile profile = new Profile();
		profile.setName("My Entity Management App");
		profile.setAddress("Spring Mvc, Java Virtual Machine, Win 10 64");
		profile.setContact("087737666614");
		profile.setWebsite("http://localhost:8080/entitymanagement");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setBackgroundUrl("DefaultBackground.BMP");
		profile.setAppCode(appCode);
		profile.setShortDescription("Entity Management");
		profile.setColor("green");
		profile.setAbout("Nam libero tempore.");
		return profile;
	}

	public <T extends BaseEntity> JpaRepository getJpaRepository(Class<T> _entityClass) {
		log.info("get JPA Repository for: {}", _entityClass);

		JpaRepository result = this.repositoryMap.get(_entityClass);

		log.info("found repo object: {}", result);

		return result;
	}

}
