
package org.objectledge.maven.connectors.coral.tests;

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

/**
 * Defines the accessor methods of <code>sample</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface SampleResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "sample";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>value</code> attribute.
     *
     * @return the value of the the <code>value</code> attribute.
     */
    public String getValue();
 
    /**
     * Sets the value of the <code>value</code> attribute.
     *
     * @param value the value of the <code>value</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setValue(String value)
        throws ValueRequiredException;
     
    // @custom methods ///////////////////////////////////////////////////////
}
