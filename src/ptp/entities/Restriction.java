package ptp.entities;

public class Restriction {

	public static enum RESTRICTION_TYPE {
		SAME_CLASS, CONTIGUOUS_CLASS, DIFFERENT_CLASS;

		public int getOrdinal() {
			return this.ordinal();
		}

		public String getName() {
			return this.name();
		}

		public static RESTRICTION_TYPE getRestrictionType(Integer rest) {
			RESTRICTION_TYPE a = null;

			switch (rest) {
			case 0:
				a = SAME_CLASS;
				break;
			case 1:
				a = CONTIGUOUS_CLASS;
				break;
			case 2:
				a = DIFFERENT_CLASS;
				break;
			default:
				break;
			}

			return a;
		}
	};

	private Product product1;
	private RESTRICTION_TYPE resType;
	private Product product2;
	
	public Restriction() {
		this.product1 = new Product();
		this.product2 = new Product();
	}

	public Product getProduct1() {
		return product1;
	}

	public void setProduct1(Product product1) {
		this.product1 = product1;
	}

	public RESTRICTION_TYPE getResType() {
		return resType;
	}

	public void setResType(RESTRICTION_TYPE resType) {
		this.resType = resType;
	}

	public Product getProduct2() {
		return product2;
	}

	public void setProduct2(Product product2) {
		this.product2 = product2;
	}
}
