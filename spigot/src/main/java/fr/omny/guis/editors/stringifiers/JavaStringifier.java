package fr.omny.guis.editors.stringifiers;

public class JavaStringifier implements Stringifier {

	@Override
	public String toString(Object object) {
		return object == null ? "null" : object.toString();
	}

}
