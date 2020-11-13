package com.fajar.entitymanagement.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemoryInfo  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2808728639915206215L;
	private long heapSize;
	private long heapMaxSize;
	private long heapFreeSize;
}
