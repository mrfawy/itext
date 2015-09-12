package com.nw.itext.pdfchanger.app;

public class App {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("Missing param operaion type , valid values are : Change , rollback ,locate");
			return;
		}
		String operation = args[0].toLowerCase();
		switch (operation) {
		case "change": {
			PDFChanger.main(null);
		}
			;
			break;
		case "rollback": {
			PDFRollBack.main(null);
		}
			;
			break;
		case "locate": {
			FileLocator.main(null);

		};
			break;

		default:
			break;
		}
	}

}
