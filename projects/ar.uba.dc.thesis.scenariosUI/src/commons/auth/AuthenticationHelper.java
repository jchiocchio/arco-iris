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
 * $Id: AuthenticationHelper.java,v 1.2 2008/01/17 20:10:56 cvschioc Exp $
 */

package commons.auth;


/**
 * Interfaz que define m�todos relacionados con la autenticaci�n de un Usuario.
 * @author Jonathan Chiocchio
 * @version $Revision: 1.2 $ $Date: 2008/01/17 20:10:56 $
 */

public interface AuthenticationHelper {

	boolean authenticate(String username, String password);
	
	public boolean tiempoRemanenteExpirado();
	
	public void cambiarPassword(String oldPassword, String newPassword);
}
