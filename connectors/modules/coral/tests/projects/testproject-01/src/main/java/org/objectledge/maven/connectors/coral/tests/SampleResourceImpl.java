
package org.objectledge.maven.connectors.coral.tests;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

/**
 * An implementation of <code>sample</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class SampleResourceImpl
    extends NodeImpl
    implements SampleResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>value</code> attribute. */
    private static AttributeDefinition valueDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>sample</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public SampleResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>sample</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static SampleResource getSampleResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof SampleResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not sample");
        }
        return (SampleResource)res;
    }

    /**
     * Creates a new <code>sample</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param value the value attribute
     * @return a new SampleResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static SampleResource createSampleResource(CoralSession session, String name,
        Resource parent, String value)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("sample");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("value"), value);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof SampleResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (SampleResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>value</code> attribute.
     *
     * @return the value of the <code>value</code> attribute.
     */
    public String getValue()
    {
        return (String)getInternal(valueDef, null);
    }
 
    /**
     * Sets the value of the <code>value</code> attribute.
     *
     * @param value the value of the <code>value</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setValue(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(valueDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute value "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////
}
