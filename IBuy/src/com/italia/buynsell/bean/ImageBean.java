package com.italia.buynsell.bean;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name="imgBean", eager=true)
@ViewScoped 
public class ImageBean {

	
	

	private final String sep = File.separator;
	private final String IMAGE_PATH = "C:" + sep + "BuyNSell" + sep + "images" + sep ;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	
}
