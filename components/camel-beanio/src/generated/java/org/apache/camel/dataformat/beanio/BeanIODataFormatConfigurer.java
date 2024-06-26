/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.dataformat.beanio;

import javax.annotation.processing.Generated;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.support.component.PropertyConfigurerSupport;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.apache.camel.maven.packaging.PackageDataFormatMojo")
@SuppressWarnings("unchecked")
public class BeanIODataFormatConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer {

    @Override
    public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {
        BeanIODataFormat dataformat = (BeanIODataFormat) target;
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "streamname":
        case "streamName": dataformat.setStreamName(property(camelContext, java.lang.String.class, value)); return true;
        case "mapping": dataformat.setMapping(property(camelContext, java.lang.String.class, value)); return true;
        case "ignoreunidentifiedrecords":
        case "ignoreUnidentifiedRecords": dataformat.setIgnoreUnidentifiedRecords(property(camelContext, boolean.class, value)); return true;
        case "ignoreunexpectedrecords":
        case "ignoreUnexpectedRecords": dataformat.setIgnoreUnexpectedRecords(property(camelContext, boolean.class, value)); return true;
        case "ignoreinvalidrecords":
        case "ignoreInvalidRecords": dataformat.setIgnoreInvalidRecords(property(camelContext, boolean.class, value)); return true;
        case "encoding": dataformat.setEncoding(property(camelContext, java.nio.charset.Charset.class, value)); return true;
        case "beanreadererrorhandlertype":
        case "beanReaderErrorHandlerType": dataformat.setBeanReaderErrorHandlerType(property(camelContext, java.lang.String.class, value)); return true;
        case "unmarshalsingleobject":
        case "unmarshalSingleObject": dataformat.setUnmarshalSingleObject(property(camelContext, boolean.class, value)); return true;
        default: return false;
        }
    }

}

