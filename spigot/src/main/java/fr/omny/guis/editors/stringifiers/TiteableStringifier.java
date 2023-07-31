package fr.omny.guis.editors.stringifiers;


import fr.omny.guis.attributes.Titeable;

public class TiteableStringifier implements Stringifier {

	@Override
	public String toString(Object object) {
		if (object == null)
			return "null";
		if (object instanceof Titeable t)
			return t.title();
		return object.toString();
	}

}
