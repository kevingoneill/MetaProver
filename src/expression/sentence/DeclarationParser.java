package expression.sentence;

import expression.Sort;

import java.util.ArrayList;

/**
 * Created by eamon on 7/28/16.
 *
 * Really Simple Parser for Declarations
 *
 * It manages 3 types of declarations
 *
 *  * Sort
 *  e.g.
 *  typedef <Sort name 0> <Parent Sort name>
 *
 *  * Const
 *  e.g.
 *  <Sort> <Const Name>
 *
 *  * Function
 *  e.g.
 *  <Return Sort> <Function Name> [<Parameter 0> ... <Parameter n>]
 *
 */

public class DeclarationParser {

    public static boolean ParseDeclaration(String s) {
      String[] arr = s.split("\\s");
      if (arr[0].equals("typedef")) {
            //If it starts with typedef it is a Sort Declaration
            if (arr.length == 0) {
              throw new ParserException("typdef requires at least one argument.");
            } else if (arr.length == 2) {
                // If no parent is provided default to object
                sortDec(arr[1], Sort.OBJECT);
            } else {
                // If parent is provided all new sorts inherit from the last element
              if (!Sort.isSort(arr[2])) {
                throw new ParserException(arr[2] + " is not defined.");
              }
              Sort parent = Sort.getSort(arr[2]);
                for (int x = 1; x < arr.length-1;++x) {
                    sortDec(arr[x], parent);
                }
            }
            return true;
        } else if (arr.length == 2) {
        //A constant is a Function with no Parameters
            if (!Sort.isSort(arr[0])) {throw new ParserException(arr[0]+" is not defined.");}
            Sort returnType = Sort.getSort(arr[0]);
        //if (returnType == Sort.BOOLEAN)
        //  functionDec(arr[1], returnType, new ArrayList<>());
        //else
          constDec(arr[1], returnType);
            return true;
        } else {
            //Everything else is a function
        ArrayList<Sort> l = new ArrayList<>();
            for (int x = 2; x < arr.length; ++x) {
                if (!Sort.isSort(arr[x])) {throw new ParserException(arr[x]+" is not defined.");}
                Sort temp = Sort.getSort(arr[x]);
                l.add(temp);
            }
            if (!Sort.isSort(arr[0])) {throw new ParserException(arr[0]+" is not defined.");}
            Sort returnType = Sort.getSort(arr[0]);
            functionDec(arr[1], returnType, l);
            return true;
        }
    }

    private static Sort sortDec(String name, Sort Parent) {
      return Sort.getSort(name, Parent);
    }

    private static Constant constDec(String name, Sort returnType) {
      return Constant.getConstant(name, returnType);
    }

    private static boolean functionDec(String name, Sort returnType, ArrayList<Sort> argTypes) {
        return Function.addDeclaration(name, returnType, argTypes);
    }
}
