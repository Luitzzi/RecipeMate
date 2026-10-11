import {App, Button, Flex, Spin} from "antd";
import {exampleApi} from "../../api/exampleApi.ts";
import type {ExampleUiDTO} from "../../generated/interface-api.ts";
import {tokens} from "../../theme/tokens.ts";
import {SectionHeader} from "../../common/components/SectionHeader.tsx";
import {useEffect, useState} from "react";
import {getApiFetchErrorMessage} from "../../api/utils/fetch.ts";

export const ExamplePage = () => {
    const {message} = App.useApp();
    const [example, setExample] = useState<ExampleUiDTO>();
    const [exampleLoading, setExampleLoading] = useState<boolean>(true);

    useEffect(() => {
        setExampleLoading(true);
        exampleApi.postExample({
            counter: 0
        }).then(result => setExample(result))
            .catch((err: Error) => message.error(getApiFetchErrorMessage("example", err.message)))
            .finally(() => setExampleLoading(false))
    }, [message])

    const incrementCount = () => {
        if (example) {
            const incrementedExample: ExampleUiDTO = {
                id: example.id,
                counter: example.counter + 1
            };
            exampleApi.putExample(incrementedExample)
                .then(result => setExample(result));
        }
    }

    if (exampleLoading)
        return <Spin />

    return (
        <>
            <SectionHeader title={"Example page"} description={"Ich bin ein counter der warum auch immer aus dem Backend geladen wird :)"} />
            <Flex vertical align={"center"} gap={tokens.gap.medium}>
                <div>
                    Current count: {example?.counter}
                </div>
                <Button type={"primary"} onClick={() => incrementCount()}>
                    Increment Count
                </ Button>
            </Flex>
        </>
    )
}
