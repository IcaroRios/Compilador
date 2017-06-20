package Model;

public final class Constants {	
	//Constantes dos tipos dos Tokens, serão usados nas análises sintáticas e semânticas
	public static final int ID_PALAVRA_RESERVADA 		= 	0,
							ID_NUMERO					=	1,
							ID_OP_ARITMETICO			=	2,
							ID_OP_RELACIONAL			=	3,
							ID_OP_DE_ATRIBUICAO			=	4,
							ID_OP_LOGICO				=	5,
							ID_DELIMITADOR				=	6,        
							ID_IDENTIFICADOR			=	7,
							ID_CARACTERE				=	8,
							ID_CADEIA_DE_CARACTERES		=	9	
							;
	//palavra reservada
	public static final int PAL_RES_PROGRAM 		= 1,
							PAL_RES_CONST			= 2,
							PAL_RES_VAR				= 3,
							PAL_RES_FUNCTION 		= 4,
							PAL_RES_BEGIN 			= 5,
							PAL_RES_END	 			= 6,
							PAL_RES_IF	 			= 7,
							PAL_RES_THEN	 		= 8,
							PAL_RES_ELSE			= 9,
							PAL_RES_WHILE 			= 10,
							PAL_RES_DO 				= 11,
							PAL_RES_READ 			= 12,
							PAL_RES_WRITE 			= 13,
							PAL_RES_INTEGER 		= 14,
							PAL_RES_REAL 			= 15,
							PAL_RES_BOOLEAN 		= 16,
							PAL_RES_TRUE 			= 17,
							PAL_RES_FALSE 			= 18,
							PAL_RES_STRING 			= 19,
							PAL_RES_CHAR 			= 20							
							;
	//op de atribuicao
	public static final int OP_ATRI_SUM		=	1,
							OP_ATRI_SUB		=	2,
							OP_ATRI_MUL		=	3,
							OP_ATRI_DIV		=	4,
							OP_ATRI_RDIV	=	5
							;
	//op relacional
	public static final int OP_REL_MAIOR_IGUAL		=	1,
							OP_REL_MENOR_IGUAL		=	2,
							OP_REL_DIFERENTE		=	3,
							OP_REL_MENOR			=	4,
							OP_REL_MAIOR			=	5,
							OP_REL_VERIFICACAO		=	6
							;
	
	//op logico
	public static final int OP_LOG_NEG		=	1,
							OP_LOG_AND		=	2,
							OP_LOG_OR		=	3
							;
							
	public static final String  PRODUCAO_VAZIA				= "#",
								PRODUCAO_FIM_DO_ARQUIVO		= "$"
							;
	//Constantes para gerar saidas
	public static final String pastaSaidaSin = "entrada";
	public static final String extensaoArquivosSin = ".oSyn";
	public static final String pastaSaidaLex = "entrada";
	public static final String extensaoArquivosLex = ".oLex";
}
