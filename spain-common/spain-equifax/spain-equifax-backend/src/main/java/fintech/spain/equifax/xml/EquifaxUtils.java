package fintech.spain.equifax.xml;

import com.google.common.base.Throwables;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class EquifaxUtils {

    private static DatatypeFactory datatypeFactory;

    public static DatatypeFactory getFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw Throwables.propagate(e);
            }
        }
        return datatypeFactory;
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(final String date) {
        if (isBlank(date)) {
            return null;
        }
        return getFactory().newXMLGregorianCalendar(date);
    }
}
