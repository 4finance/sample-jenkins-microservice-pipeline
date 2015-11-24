package io.fourfinanceit.pipeline.example

import io.fourfinanceit.pipeline.example.util.JobSpecTrait
import io.fourfinanceit.pipeline.example.util.XmlComparator
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.JobParent
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Marcin Grzejszczak
 * @author Szymon Homa
 */
abstract class AbstractViewXmlComparingSpec extends Specification implements JobSpecTrait, XmlComparator {

    static private JobParent jobParent

    def setupSpec() {
        jobParent = createJobParent()
        setupViews(jobParent)
    }

    final void 'should create exactly the expected views'() {
        expect:
            List<String> actualViews = jobParent.referencedViews*.name
            actualViews == expectedViews
    }

    @Unroll
    final void 'should generate proper XML for view "#view.name"'() {
        expect:
            String fileName = view.name + '.xml'
            def file = new File(expectedViewXmlsPath, fileName)
            compareXmls(file.path, view.node)

        where:
            view << jobParent.referencedViews
    }

    abstract List<String> getExpectedViews()

    abstract void setupViews(DslFactory dslFactory)

    abstract String getExpectedViewXmlsPath()
}
