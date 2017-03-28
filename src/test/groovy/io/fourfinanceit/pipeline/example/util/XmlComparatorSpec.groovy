package io.fourfinanceit.pipeline.example.util

import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

/**
 * @author Artur Gajowy
 * @author Marcin Grzejszczak
 */
class XmlComparatorSpec extends Specification implements XmlComparator {

    @RestoreSystemProperties
    def 'should spot differences in elements sequence'() {
        given:
            def publishJunit = '<step name="Publish Junit"/>'
            def build = '<step name="Build"/>'

        when:
            System.setProperty('outputActualXml', 'false') //so that no garbage is output
            compareXmls(
                'whatever.xml',
                "<project><steps>${build}${publishJunit}</steps></project>",
                "<project><steps>${publishJunit}${build}</steps></project>",
                false
            )

        then:
            thrown(XmlComparator.XmlsAreNotSimilar)
    }
}
