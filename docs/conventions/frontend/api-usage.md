## Endpoint calls

- The api-interfaces the frontend receives from the backend are automatically 
    generated in src/generated/interface-api.ts
- To implement an api call write a specific exampleApi.ts file following this guideline:

```ts
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
```

## Usage inside a component

There are predefined hooks such as **useFetchOnce**, or **useFetch** when you just need 
to load and display data.
If the data is added to in the frontend just write you own useEffect hook loading and handling the data.