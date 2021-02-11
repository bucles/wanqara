package com.wanqarausuarios.modelo;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Usuario.class)
public abstract class Usuario_ {

	public static volatile SingularAttribute<Usuario, String> usuNombre;
	public static volatile SingularAttribute<Usuario, String> usuUsuario;
	public static volatile SingularAttribute<Usuario, Date> usuFechaNacimiento;
	public static volatile SingularAttribute<Usuario, Integer> usuId;
	public static volatile SingularAttribute<Usuario, String> usuEmail;
	public static volatile SingularAttribute<Usuario, String> usuApellido;
	public static volatile SingularAttribute<Usuario, byte[]> usuImagen;
	public static volatile SingularAttribute<Usuario, String> usuPassword;

}

