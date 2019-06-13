package com.kaspersky.kaspresso.testcases.sections

import com.kaspersky.kaspresso.configurator.Configurator
import com.kaspersky.kaspresso.testcases.api.base.BaseTestCase
import com.kaspersky.kaspresso.testcases.core.TestContext
import com.kaspersky.kaspresso.testcases.core.TestRunner
import com.kaspersky.kaspresso.testcases.models.TestBody

class MainTestSection<InitData, Data> internal constructor(
    private val configurator: Configurator,
    private val testBodyBuilder: TestBody.Builder<InitData, Data>
) : InitSection<InitData, Data>, TransformSection<Data> {

    /**
     * Runs:
     * 1) [BeforeTestSection],
     * 2) Optional [init],
     * 3) Optional [transform]'s sections (only if [init] was called before),
     * 4) [MainTestSection]'s steps,
     * 5) [AfterTestSection]. [AfterTestSection] is invoked even if [BeforeTestSection] or [BaseTestCase]'s [steps] failed.
     *
     * @param steps steps to run.
     */
    override fun run(steps: TestContext<Data>.() -> Unit) {
        val testBody = testBodyBuilder.apply { this.steps = steps }.build()
        TestRunner<InitData, Data>(configurator).run(testBody)
    }

    /**
     * Can be invoked after [BeforeTestSection]. Running to init test data using dsl.
     *
     * @param actions actions to be wrapped and invoked before the test.
     * @return [TransformSection] to continue building a test.
     */
    override fun init(actions: InitData.() -> Unit): TransformSection<Data> {
        testBodyBuilder.apply { initActions = actions }
        return this
    }

    /**
     * Can be invoked after [BeforeTestSection] and [init] but before [MainTestSection].
     *
     * It's possible to add multiple transform blocks.
     *
     * @param actions actions to run.
     * @return [TransformSection] to continue building a test.
     */
    override fun transform(actions: Data.() -> Unit): TransformSection<Data> {
        testBodyBuilder.apply { transformActionsList.add(actions) }
        return this
    }
}