package org.zend.php.zendserver.deployment.ui.editors.propertiestext;

/**
 * Properties file partitioning definition.
 * <p>
 * A property key is represented by the
 * {@link org.eclipse.jface.text.IDocument#DEFAULT_CONTENT_TYPE default
 * partition}.
 * </p>
 */
public interface IPropertiesFilePartitions {

	/**
	 * The name of the properties file partitioning. Value: {@value}
	 */
	String PROPERTIES_FILE_PARTITIONING = "___pf_partitioning"; //$NON-NLS-1$

	/**
	 * The name of a comment partition. Value: {@value}
	 */
	String COMMENT = "__pf_comment"; //$NON-NLS-1$

	/**
	 * The name of a property value partition.
	 * <p>
	 * Note: The value partition may contain assignment characters at their
	 * beginning
	 * </p>
	 * Value: {@value}
	 */
	String PROPERTY_VALUE = "__pf_roperty_value"; //$NON-NLS-1$

	/**
	 * Array with properties file partitions. Value: {@value}
	 */
	String[] PARTITIONS = new String[] { COMMENT, PROPERTY_VALUE };

}
