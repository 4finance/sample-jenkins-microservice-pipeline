package io.fourfinanceit.pipeline.example.util

import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.ComparisonFailure

import java.nio.file.Paths

/**
 * @author Artur Gajowy
 * @author Marcin Grzejszczak
 */
//TODO package is as a separate test lib
//TODO rethink the way files are accessed in this class
trait XmlComparator {

    private XMLUnit xmlUnit

    @Before
    void init() {
        xmlUnit = new XMLUnit()
        xmlUnit.ignoreWhitespace = true
        xmlUnit.normalizeWhitespace = true
    }

    void compareXmls(String packageFileName, Node nodeToCompare) {
        //default parameter initializers are not allowed in traits
        compareXmls(packageFileName, nodeToCompare, false)
    }

    void compareXmls(String packageFileName, Node nodeToCompare, boolean displayActualXmlInCaseOfError) {
        String nodeXml = XmlUtil.serialize(nodeToCompare).stripIndent().stripMargin()
        def referenceXmlFile = getFileOrNull(packageFileName)
        if (!referenceXmlFile) {
            if (System.getProperty('outputMissingXml') == 'true') {
                def missingXml = new File("./src/test/resources/${packageFileName}")
                missingXml.parentFile.mkdirs()
                missingXml.text = nodeXml
            }
            throw new RuntimeException("Reference xml file [$packageFileName] not found")
        }
        String referenceXml = XmlUtil.serialize(referenceXmlFile.text).stripIndent().stripMargin()
        compareXmls(packageFileName, referenceXml, nodeXml, displayActualXmlInCaseOfError)
    }

    void compareXmls(String packageFileName, String referenceXml, String nodeXml, boolean displayActualXmlInCaseOfError) {
        Diff diff = xmlUnit.compareXML(referenceXml, nodeXml)
        diff.overrideElementQualifier(new ElementNameAndAttributeQualifier())
        if (!diff.identical()) {
            DetailedDiff detailedDiff = new DetailedDiff(diff)
            //TODO: How to get line from diff? Find by node in XML file?
            if (displayActualXmlInCaseOfError) {
                println("Actual XML:\n $nodeXml")
            }
            if (System.getProperty("outputActualXml") == 'true') {
                new File("src/test/resources/${packageFileName}.ACTUAL.xml").text = nodeXml
            }
            throw new XmlsAreNotSimilar(packageFileName, detailedDiff.allDifferences, referenceXml, nodeXml)
        }
    }

    private File getFileOrNull(String path) {
        URI uri = getClass()?.getResource(path)?.toURI()
        uri ? new File(uri) : null
    }

    static class XmlsAreNotSimilar extends ComparisonFailure {
        XmlsAreNotSimilar(String packageFileName, List diffs, String expected, String actual) {
            super("For file ${formatPackageFileNameToHaveClickableLinkInIdea(packageFileName)} the following differences where found [$diffs].",
                expected, actual)
        }

        private static String formatPackageFileNameToHaveClickableLinkInIdea(String packageFileName) {
            //.(foo.ext:1) is a regex recognizable by Idea
            //In addition as there usually is "at" word in the exception message later on it is required to add extra "at" before a file name
            return "at .(${Paths.get(packageFileName).fileName}:1) "
        }
    }
}
