package ptp.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.stereotype.Component;

import ptp.ag.AG;
import ptp.ag.entities.Individual;
import ptp.ag.entities.Population;
import ptp.entities.Product;
import ptp.entities.Restriction;
import ptp.entities.Restriction.RESTRICTION_TYPE;
import ptp.properties.PropertiesBean;

@Component
@ManagedBean(name = "pTPController", eager = true)
@SessionScoped
public class PTPController implements Serializable  {

	private static final long serialVersionUID = 1L;

	private PropertiesBean propertiesBean = new PropertiesBean();

	private List<Product> products = new ArrayList<Product>();

	private Population population;

	private String product1;
	private String product2;

	public String validateInputFile(FileUploadEvent event) {
		if (event.getFile() != null) {

			if (validateFile(event.getFile())) {
				this.setFile(event.getFile());
				FacesMessage message = new FacesMessage("Succesful " + event.getFile().getFileName() + " is uploaded.",
						"");
				FacesContext.getCurrentInstance().addMessage(null, message);
			} else {
				FacesMessage message = new FacesMessage("Uploaded File not Valid!");
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
		} else {
			FacesMessage message = new FacesMessage("Unsuccesfully upload!");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return "";
	}

	public String runGA() {
		String ret = "";
		if (this.getFile() != null) {
			try {
				if (AG.validateStorage(this.propertiesBean)) {
					population = AG.runAG(this.products, this.propertiesBean);

					PrintStream ps = new PrintStream(
							new FileOutputStream(new File("/var/www/files/populacao.txt")));
					
					/*PrintStream ps = new PrintStream(
							new FileOutputStream(new File("C:\\_projetos\\PICKER-TO-PARTS\\populacao.txt")));
					*/
					for (Individual ind : population.getPopulation()) {
						ps.print(ind);
					}

					ps.flush();
					ps.close();

					FacesMessage message = new FacesMessage("GA Executed!");
					FacesContext.getCurrentInstance().addMessage(null, message);
				} else {
					FacesMessage message = new FacesMessage("GA Execution Error: Invalid Storage!");
					FacesContext.getCurrentInstance().addMessage(null, message);
				}
			} catch (Exception ex) {
				FacesMessage message = new FacesMessage("GA Execution Error: " + ex.getMessage());
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
		} else {
			FacesMessage message = new FacesMessage("File Not Uploaded!");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return ret;
	}

	public String ranking() {

		return "";
	}

	public String uploadNewFile() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "index.xhtml?faces-redirect=true";
	}

	public String addRestriction(Integer resType) {
		Restriction r = new Restriction();

		r.getProduct1().setName(this.product1);
		r.getProduct2().setName(this.product2);
		r.setResType(RESTRICTION_TYPE.getRestrictionType(resType));

		if (validateRestriction(r)) {
			this.propertiesBean.getRestrictions().add(r);
			this.product1 = "";
			this.product2 = "";
		} else {
			FacesMessage message = new FacesMessage("Invalid Restriction!");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return "";
	}

	public String removeRestriction(Restriction r) {
		this.propertiesBean.getRestrictions().remove(r);
		return "";
	}

	public String backIndex() {
		return "index.xhtml";
	}

	public String listProducts() {
		return "listProducts.xhtml";
	}

	public List<String> completeText(String query) {
		List<String> results = new ArrayList<String>();
		for (Product p : this.products) {

			if (p.getName().startsWith(query)) {
				results.add(p.getName());
			}
		}

		return results;
	}

	// private methods
	private void setFile(UploadedFile file) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("uploadedFile", file);
	}

	private UploadedFile getFile() {
		return (UploadedFile) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("uploadedFile");
	}

	private boolean validateFile(UploadedFile file) {
		boolean ret = true;
		try {
			XSSFWorkbook myWorkBook = new XSSFWorkbook(file.getInputstream());
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			rowIterator.next();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				Product p = new Product();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int cellIndex = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if ((cellIndex == 0) && (cell.getCellType() != Cell.CELL_TYPE_NUMERIC)) {
						p = null;
						break;
					}
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						if (cellIndex == 1) {
							p.setName(cell.getStringCellValue());
						}
						break;
					case Cell.CELL_TYPE_NUMERIC:
						switch (cellIndex) {
						case 0:
							p.setNumber(new Double(cell.getNumericCellValue()).intValue());
							break;
						case 2:
							p.setSpace1(cell.getNumericCellValue());
							break;
						case 3:
							p.setSpace2(cell.getNumericCellValue());
							break;
						case 4:
							p.setSpace3(cell.getNumericCellValue());
							break;
						case 5:
							p.setSpace4(cell.getNumericCellValue());
							break;
						case 6:
							p.setPopularity(cell.getNumericCellValue());
							break;
						case 7:
							p.setDemand(cell.getNumericCellValue());
							break;
						case 8:
							p.setClient(cell.getNumericCellValue());
							break;
						default:
							break;
						}
						break;
					default:
						throw new Exception("Invalid file!");
					}
					cellIndex++;
				}
				if (p != null) {
					if (!p.validateProduct()) {
						throw new Exception("Invalid file!");
					}
					this.products.add(p);
				}
			}
		} catch (IOException e) {
			ret = false;
		} catch (Exception e) {
			ret = false;
		}

		return ret;
	}

	private boolean validateRestriction(Restriction r) {
		boolean p1 = false;
		boolean p2 = false;
		boolean ret = false;

		for (Product p : products) {
			if (!p1 && p.getName().equals(r.getProduct1().getName())) {
				p1 = true;
			}
			if (!p2 && p.getName().equals(r.getProduct2().getName())) {
				p2 = true;
			}
			if (p1 && p2) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	public PropertiesBean getPropertiesBean() {
		return propertiesBean;
	}

	public void setPropertiesBean(PropertiesBean propertiesBean) {
		this.propertiesBean = propertiesBean;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Population getPopulation() {
		return population;
	}

	public String getProduct1() {
		return product1;
	}

	public void setProduct1(String product1) {
		this.product1 = product1;
	}

	public String getProduct2() {
		return product2;
	}

	public void setProduct2(String product2) {
		this.product2 = product2;
	}
}
