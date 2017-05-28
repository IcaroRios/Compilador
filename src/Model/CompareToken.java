/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.Comparator;

/**
 *
 * @author Neida
 */
public class CompareToken implements Comparator {

    public CompareToken() {
    }

    /**
     * deve retornar 1,= ou -1
     *
     * @param t
     * @param t1
     * @return
     */
    @Override
    public int compare(Object t, Object t1) {
        Token tok;
        if (t instanceof Token) {
            tok = (Token)t;
            String s = (String) t1;
            s = s.substring(1, t1.toString().length()-1);
            
            if(tok.getLexema().equals(s)){
                return 1;
            }
            return -1;
        } else {
            return 0;
        }
        
    }

}
