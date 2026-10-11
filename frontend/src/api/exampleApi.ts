import {buildURL} from "./utils/urlBuilder.ts";
import {get, post, put} from "./utils/fetch.ts";
import type {ExampleUiDTO} from "../generated/interface-api.ts";

const basePath = "example";

const getExampleById = (id: string) => {
    const requestUrl = buildURL(`${basePath}/${id}`);
    return get<ExampleUiDTO>(requestUrl.toString());
}

const postExample = (example: ExampleUiDTO) => {
    const requestUrl = buildURL(basePath);
    return post<ExampleUiDTO>(requestUrl.toString(), example);
}

const putExample = (example: ExampleUiDTO) => {
    const requestUrl = buildURL(`${basePath}/${example.id}`);
    return put<ExampleUiDTO>(requestUrl.toString(), example)
}

export const exampleApi = {
    getExampleById,
    postExample,
    putExample
} as const;