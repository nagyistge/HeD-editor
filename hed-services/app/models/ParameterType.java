package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * User: rk Date: 8/13/13 Package: models
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterType
        extends SharpType implements Comparable<ParameterType>
{
    @Id
    public String key;

//    public String name;   // e.g., MEDICATION

    public String label;

    public String description;

    public String hedTypeName;   // one of literalexpression.xsd types.

    public HedType hedType;

    public List<ElementType> elements = new ArrayList<ElementType>();

    public Boolean useLiteral = true;

    public List<String> expressionChoices = new ArrayList<String>();

    public List<String> operationChoices = new ArrayList<String>();

    public String selectedOperation = null;

    public String selectedExpression = null;

    public String displayValue;

    public boolean optional;

    public boolean multiple;

    public String path;

    public int index;

    //========================================================================================

    public ElementType getElement(String name)
    {
        for ( ElementType e : elements )
        {
            if ( name.equals( e.name ) )
            {
                return e;
            }
        }
        return null;
    }

    public ParameterInst createInst()
    {
        ParameterInst inst = new ParameterInst();
        inst.type = this;
//        inst.id = ModelHome.createUUID();
        inst.useLiteral = this.useLiteral;
        inst.selectedExpression = this.selectedExpression;

        for ( ElementType et : this.elements )
        {
            ElementInst e = et.createInst();
            inst.elements.add( e );
        }

        return inst;
    }


    @Override
    public int compareTo( ParameterType o ) {
        return this.index - o.index;
    }
}
