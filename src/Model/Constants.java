package Model;

public final class Constants {
	//Constantes dos tipos dos Tokens, serão usados nas análises sintáticas e semânticas
	public static final int TIPO_PALAVRA_RESERVADA 		= 	0,
							TIPO_NUMERO					=	1,
							TIPO_OPERADOR_ARITMETICO	=	2,
							TIPO_OPERADOR_RELACIONAL	=	3,
							TIPO_OPERADOR_DE_ATRIBUICAO	=	4,
							TIPO_OPERADOR_LOGICO		=	5,
							TIPO_DELIMITADOR			=	6,        
							TIPO_IDENTIFICADOR			=	7,
							TIPO_CARACTERE				=	8,
							TIPO_CADEIA_DE_CARACTERES	=	9	
							;

	public static final int PALAVRA_RESERVADA_PROGRAM 		= 1,
							PALAVRA_RESERVADA_CONST			= 2,
							PALAVRA_RESERVADA_VAR			= 3,
							PALAVRA_RESERVADA_FUNCTION 		= 4,
							PALAVRA_RESERVADA_BEGIN 		= 5,
							PALAVRA_RESERVADA_END	 		= 6,
							PALAVRA_RESERVADA_IF	 		= 7,
							PALAVRA_RESERVADA_THEN	 		= 8,
							PALAVRA_RESERVADA_ELSE			= 9,
							PALAVRA_RESERVADA_WHILE 		= 10,
							PALAVRA_RESERVADA_DO 			= 11,
							PALAVRA_RESERVADA_READ 			= 12,
							PALAVRA_RESERVADA_WRITE 		= 13,
							PALAVRA_RESERVADA_INTEGER 		= 14,
							PALAVRA_RESERVADA_REAL 			= 15,
							PALAVRA_RESERVADA_BOOLEAN 		= 16,
							PALAVRA_RESERVADA_TRUE 			= 17,
							PALAVRA_RESERVADA_FALSE 		= 18,
							PALAVRA_RESERVADA_STRING 		= 19,
							PALAVRA_RESERVADA_CHAR 			= 20							
							;	
	public static final String PRODUCAO_VAZIA	= "$"
							;
}
