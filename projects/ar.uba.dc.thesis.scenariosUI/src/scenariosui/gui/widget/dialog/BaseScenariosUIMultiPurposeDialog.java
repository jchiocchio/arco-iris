/*
 * Licencia de Caja de Valores S.A., Versi�n 1.0
 *
 * Copyright (c) 2006 Caja de Valores S.A.
 * 25 de Mayo 362, Ciudad Aut�noma de Buenos Aires, Rep�blica Argentina
 * Todos los derechos reservados.
 *
 * Este software es informaci�n confidencial y propietaria de Caja de Valores S.A. ("Informaci�n
 * Confidencial"). Usted no divulgar� tal Informaci�n Confidencial y la usar� solamente de acuerdo a
 * los t�rminos del acuerdo de licencia que posee con Caja de Valores S.A.
 */

/*
 * $Id: BaseEmesMultiPurposeDialog.java,v 1.8 2008/02/26 14:45:34 cvspasto Exp $
 */

package scenariosui.gui.widget.dialog;

import sba.common.properties.EnumProperty;
import sba.common.properties.FakeEnumProperty;
import scenariosui.gui.util.purpose.ScenariosUIPurpose;

import commons.gui.widget.dialog.BaseScenariosUIDialog;
import commons.properties.CommonLabels;

/**
 * Modela un di�logo b�sico d�nde se puede saber si el mismo corresponde a un di�logo de Alta, Edici�n u otro tipo ,
 * determinado por un tipo enumerado adecuado para tal fin.
 * 
 * @author Jonathan Chiocchio
 * @version $Revision: 1.8 $ $Date: 2008/02/26 14:45:34 $
 */

public abstract class BaseScenariosUIMultiPurposeDialog<T> extends BaseScenariosUIDialog<T> {

	public BaseScenariosUIMultiPurposeDialog(T model, EnumProperty title, ScenariosUIPurpose purpose) {
		super(model, new FakeEnumProperty(title.toString() + " - " + purpose.toString()), purpose.isReadOnly());
		super.readOnly = this.isReadOnly(super.getModel(), purpose);
		this.purpose = purpose;
	}

	/**
	 * Indica el estado de readOnly. Por defecto, el mismo est� dado por el prop�sito, aunque dicho comportamiento puede
	 * ser sobreescrito ante la necesidad de evaluar m�s condiciones dependientes del modelo.
	 * 
	 * @param model
	 *            el modelo involucrado en la apeertura del di�logo.
	 * @param scenariosUIPurpose
	 *            el pro�sito con el que se abre el di�logo.
	 */
	protected boolean isReadOnly(T model, ScenariosUIPurpose scenariosUIPurpose) {
		return scenariosUIPurpose.isReadOnly();
	}

	@Override
	protected boolean isOkButtonAllowed() {
		boolean sinBotonAceptar = (this.isReadOnly(super.getModel(), this.purpose) && this.purpose
				.equals(ScenariosUIPurpose.EDIT))
				|| this.purpose.equals(ScenariosUIPurpose.VIEW);
		return !sinBotonAceptar;
	}

	@Override
	protected EnumProperty getAcceptButtonText() {
		EnumProperty result = super.getAcceptButtonText();
		EnumProperty acceptEnumProp = this.purpose.getAcceptButtonText();
		if (acceptEnumProp != null) {
			result = acceptEnumProp;
		}
		return result;
	}

	/**
	 * Provee el texto para el bot�n "Cancelar". Por defecto, este valor est� dado por el prop�sito, siempre y cuando no
	 * sea nulo. Si dicho valor fuera nulo, y no existe el bot�n "Aceptar", el texto es el dado por
	 * {@link #CLOSE(CommonLabels)}.<br>
	 * Si no se cumplen las anteriores condiciones, el texto es el dado por la superclase
	 * 
	 * @see {@link #getCancelButtonText()BaseEmesDialog}
	 */
	@Override
	protected EnumProperty getCancelButtonText() {
		EnumProperty result = super.getCancelButtonText();

		EnumProperty cancelEnumProp = this.purpose.getCancelButtonText();
		if (cancelEnumProp != null) {
			result = cancelEnumProp;
		} else if (!this.isOkButtonAllowed()) {
			result = CommonLabels.CLOSE;
		}
		return result;
	}

	protected ScenariosUIPurpose purpose;
}