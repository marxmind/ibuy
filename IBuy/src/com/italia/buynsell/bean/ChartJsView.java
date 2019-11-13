package com.italia.buynsell.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import com.italia.buynsell.controller.CornConditions;
import com.italia.buynsell.controller.Debit;
import com.italia.buynsell.controller.PurchasingCorn;
import com.italia.buynsell.utils.DateUtils;

@ManagedBean
@ViewScoped
public class ChartJsView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 564658341L;
	
	private PieChartModel pieModel;
	private BarChartModel barModel2;
	private LineChartModel lineModel;
	
	@PostConstruct
	public void init() {
		
		String sql="SELECT *  FROM purchasingcorn WHERE datein=?";
        String[] params = new String[1];
        params[0] = DateUtils.getCurrentDateYYYYMMDD();
		List<PurchasingCorn> corns = PurchasingCorn.retrievePurchasingCorn(sql, params);
		
		createPieModel(corns);
		
		sql="SELECT *  FROM purchasingcorn WHERE datein>=? AND datein<=?";
        params = new String[2];
        params[0] = DateUtils.getCurrentYear()+"-01-01";
        params[1] = DateUtils.getCurrentYear()+"-12-31";
        List<PurchasingCorn> montlyCorns = PurchasingCorn.retrievePurchasingCorn(sql, params);
		createBarModel2(montlyCorns);
		
		createLineModel();
	}
	
	private String[] rgbColors() {
    	String[] rgb = {"rgb(255, 99, 132)","rgb(250, 53, 95)","rgb(183, 83, 104)","rgb(123, 70, 82)","rgb(99, 3, 23)",
				"rgb(54, 162, 235)","rgb(14, 122, 195)","rgb(8, 73, 116)","rgb(88, 109, 124)","rgb(195, 227, 249)",
				"rgb(255, 205, 86)","rgb(228, 165, 14)","rgb(169, 144, 84)","rgb(244, 227, 187)","rgb(180, 177, 170)",
				"rgb(116, 243, 143)","rgb(117, 165, 127)","rgb(9, 48, 17)","rgb(123, 118, 240)","rgb(14, 2, 249)",
				"rgb(138, 132, 246)","rgb(67, 62, 171)","rgb(46, 238, 200)","rgb(21, 190, 156)","rgb(6, 147, 119)",
				"rgb(4, 70, 57)","rgb(26, 75, 65)","rgb(121, 172, 162)","rgb(204, 248, 239)","rgb(234, 204, 248)",
				"rgb(219, 158, 247)","rgb(203, 111, 246)","rgb(188, 58, 248)","rgb(167, 6, 242)","rgb(128, 20, 178)",
				"rgb(178, 20, 143)","rgb(190, 55, 160)","rgb(217, 106, 192)","rgb(239, 156, 220)","rgb(247, 218, 241)",
				"rgb(253, 247, 233)","rgb(248, 223, 169)","rgb(234, 191, 100)","rgb(207, 152, 32)","rgb(144, 120, 69)",
				"rgb(156, 142, 113)","rgb(141, 137, 130)","rgb(175, 213, 205)","rgb(110, 181, 167)","rgb(57, 143, 126)"
				};
    	
    	return rgb;
    }
	
	public void createLineModel() {
		lineModel = new LineChartModel();
        ChartData data = new ChartData();
         
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        
        String sql="SELECT *  FROM expenses WHERE datein>=? AND datein<=?";
        String[] params = new String[2];
        params[0] = DateUtils.getCurrentYear()+"-01-01";
        params[1] = DateUtils.getCurrentYear()+"-12-31";
        
        Map<Integer, Double> deb = Collections.synchronizedMap(new HashMap<Integer, Double>());
		for(Debit d : Debit.retrieveDebit(sql, params)){
			int month = Integer.valueOf(d.getDateIn().split("-")[1]);
			double amount = 0;
			try {amount = Double.valueOf(d.getAmountIn().replace(",", ""));}catch(Exception e) {}
			
			if(deb!=null && deb.containsKey(month)) {
				amount += deb.get(month);
				deb.put(month, amount);
			}else {
				deb.put(month, amount);
			}
			
		}
		
		List<String> labels = new ArrayList<>();
		for(int month=1; month<=DateUtils.getCurrentMonth(); month++) {
			labels.add(DateUtils.getMonthName(month));
			if(deb!=null && deb.containsKey(month)) {
				values.add(deb.get(month));
			}
			
		}
        
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Monthly Expenses");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        
        data.setLabels(labels);
         
        //Options
        LineChartOptions options = new LineChartOptions();        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Monthly Expenses");
        options.setTitle(title);
         
        lineModel.setOptions(options);
        lineModel.setData(data);
	}
	
	public void createBarModel2(List<PurchasingCorn> montlyCorns) {
        barModel2 = new BarChartModel();
        ChartData data = new ChartData();
         
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Yellow");
        barDataSet.setBackgroundColor("rgba(255, 159, 64, 0.2)");
        barDataSet.setBorderColor("rgb(255, 159, 64)");
        barDataSet.setBorderWidth(1);
        List<Number> values = new ArrayList<>();
        
         
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("White");
        barDataSet2.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        barDataSet2.setBorderColor("rgb(255, 99, 132)");
        barDataSet2.setBorderWidth(1);
        List<Number> values2 = new ArrayList<>();
       
         
        List<String> labels = new ArrayList<>();
        
        
        Map<Integer, Double> monthsWhite = Collections.synchronizedMap(new HashMap<Integer, Double>());
        Map<Integer, Double> monthsYellow = Collections.synchronizedMap(new HashMap<Integer, Double>());
        
        for(PurchasingCorn p : montlyCorns) {
        	String color = p.getCorncolor();
        	int month = Integer.valueOf(p.getDateIn().split("-")[1]);
        	
        	if("1".equalsIgnoreCase(color)) {//white else yellow
        		
        		if(monthsWhite!=null && monthsWhite.containsKey(month)) {
        			double amount = monthsWhite.get(month);
        			amount += p.getAmount().doubleValue();
        			monthsWhite.put(month, amount);
        		}else {
        			monthsWhite.put(month, p.getAmount().doubleValue());
        		}
        		
        	}else {
        		if(monthsYellow!=null && monthsYellow.containsKey(month)) {
        			double amount = monthsYellow.get(month);
        			amount += p.getAmount().doubleValue();
        			monthsYellow.put(month, amount);
        		}else {
        			monthsYellow.put(month, p.getAmount().doubleValue());
        		}
        	}
        }
        
        
        for(int month =1; month <= DateUtils.getCurrentMonth(); month++) {
        	labels.add(DateUtils.getMonthName(month));
        	
        	if(monthsWhite!=null && monthsWhite.containsKey(month)) {
        		values.add(monthsWhite.get(month));
        	}
        	
        	if(monthsYellow!=null && monthsYellow.containsKey(month)) {
        		values2.add(monthsYellow.get(month));
        	}
        	
        }
        
        
        barDataSet.setData(values);
        barDataSet2.setData(values2);
        
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        
        data.setLabels(labels);
        barModel2.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Monthly buying of corn");
        options.setTitle(title);
         
        barModel2.setOptions(options);
    }
	
	public void createPieModel(List<PurchasingCorn> corns) {
		pieModel = new PieChartModel();
        ChartData data = new ChartData();
        
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        double wcd = 0.0;
        double w_wet = 0.0;
        double w_bilog = 0.0;
        double w_semi_basa = 0.0;
        
        double y_dry = 0.0;
        double y_basa = 0.0;
        double y_bilog = 0.0;
        double y_semi_basa = 0.0;
        
        double white_total = 0.0;
        double yellow_total = 0.0;
        
        for(PurchasingCorn corn : corns) {
        	String key = corn.getConditions();
        	
        	if(CornConditions.WCD_NATIVE.getCode().equalsIgnoreCase(key)) {
        		wcd +=corn.getAmount().doubleValue();
        	}else if(CornConditions.WHITE_WET.getCode().equalsIgnoreCase(key)) {
        		w_wet +=corn.getAmount().doubleValue();
        	}else if(CornConditions.WHITE_BILOG.getCode().equalsIgnoreCase(key)) {
        		w_bilog +=corn.getAmount().doubleValue();
        	}else if(CornConditions.WHITE_SEMI_BASA.getCode().equalsIgnoreCase(key)) {
        		w_semi_basa +=corn.getAmount().doubleValue();	
        	}else if(CornConditions.YELLOW_DRY.getCode().equalsIgnoreCase(key)) {
        		y_dry +=corn.getAmount().doubleValue();
        	}else if(CornConditions.YELLOW_BASA.getCode().equalsIgnoreCase(key)) {
        		y_basa +=corn.getAmount().doubleValue();
        	}else if(CornConditions.YELLOW_BILOG.getCode().equalsIgnoreCase(key)) {
        		y_bilog +=corn.getAmount().doubleValue();
        	}else if(CornConditions.YELLOW_SEMI_BASA.getCode().equalsIgnoreCase(key)) {
        		y_semi_basa +=corn.getAmount().doubleValue();
        	}
        	
        	if("1".equalsIgnoreCase(corn.getCorncolor())) {//white else yellow
        		white_total += corn.getAmount().doubleValue();
        	}else {
        		yellow_total += corn.getAmount().doubleValue();
        	}
        	
        }
        
        data.setLabels("WHITE("+white_total+") | YELLOW("+yellow_total+")");
        
        int indexColor=0;
        String[] rgb = rgbColors();
        String labelAmount = "";
        if(wcd>0) {
        	values.add(wcd);
        	labelAmount = "(" + wcd + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.WCD_NATIVE.getName()+labelAmount);
        }
        
        if(w_wet>0) {
        	values.add(w_wet);
        	labelAmount = "(" + w_wet + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.WHITE_WET.getName()+labelAmount);
        }
        if(w_bilog>0) {
        	values.add(w_bilog);
        	labelAmount = "(" + w_bilog + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.WHITE_BILOG.getName()+labelAmount);
        }
        if(w_semi_basa>0) {
        	values.add(w_semi_basa);
        	labelAmount = "(" + w_semi_basa + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.WHITE_SEMI_BASA.getName()+labelAmount);
        }
        
        if(y_dry>0) {
        	values.add(y_dry);
        	labelAmount = "(" + y_dry + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.YELLOW_DRY.getName()+labelAmount);
        }
        if(y_basa>0) {
        	values.add(y_basa);
        	labelAmount = "(" + y_basa + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.YELLOW_BASA.getName()+labelAmount);
        }
        
        if(y_bilog>0) {
        	values.add(y_bilog);
        	labelAmount = "(" + y_bilog + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.YELLOW_BILOG.getName()+labelAmount);
        }
        if(y_semi_basa>0) {
        	values.add(y_semi_basa);
        	labelAmount = "(" + y_semi_basa + ")";
        	bgColors.add(rgb[indexColor++]);
        	labels.add(CornConditions.YELLOW_SEMI_BASA.getName()+labelAmount);
        }
		
		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColors);
		data.addChartDataSet(dataSet);
		data.setLabels(labels);
		
        pieModel.setData(data);
	}
	
	public PieChartModel getPieModel() {
		return pieModel;
	}

	public void setPieModel(PieChartModel pieModel) {
		this.pieModel = pieModel;
	}

	public BarChartModel getBarModel2() {
		return barModel2;
	}

	public void setBarModel2(BarChartModel barModel2) {
		this.barModel2 = barModel2;
	}

	public LineChartModel getLineModel() {
		return lineModel;
	}

	public void setLineModel(LineChartModel lineModel) {
		this.lineModel = lineModel;
	}
	

}
