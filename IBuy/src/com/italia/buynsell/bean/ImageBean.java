package com.italia.buynsell.bean;


import java.io.File;
import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("imgBean")
@ViewScoped
public class ImageBean implements Serializable{

	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5346597658571L;
	private final String sep = File.separator;
	private final String IMAGE_PATH = "C:" + sep + "BuyNSell" + sep + "images" + sep ;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	
}
