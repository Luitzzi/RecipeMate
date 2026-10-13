import {useState} from "react";
import {useEffectOnce} from "./useEffectOnce.ts";
import {useFetch} from "./useFetch.ts";

export const useFetchOnce = <T>(
    fetch: () => Promise<T>,
): [T | undefined, boolean, Error | undefined] => {
    return useFetch(fetch, [])
};
