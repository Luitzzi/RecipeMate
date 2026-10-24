/* tslint:disable */
/* eslint-disable */

export interface ChatRequestUiDTO {
    message: string;
    currentView: CurrentViewUiDTO;
}

export interface ChatResponseUiDTO {
    message: string;
    status: ChatResponseStatus;
}

export interface CounterUiDTO {
    id: string;
    count: number;
}

export interface CurrentViewUiDTO {
    type: ViewType;
    id?: string;
}

export interface HttpClient {

    request<R>(requestConfig: { method: string; url: string; queryParams?: any; data?: any; copyFn?: (data: R) => R; options?: RequestInit; }): RestResponse<R>;
}

export class CounterUiControllerClient {

    constructor(protected httpClient: HttpClient) {
    }

    /**
     * HTTP POST /api/counter
     * Java method: de.example.backend.boundary.controller.CounterUiController.create
     */
    create(options?: RequestInit): RestResponse<CounterUiDTO> {
        return this.httpClient.request({ method: "POST", url: uriEncoding`api/counter`, options: options });
    }

    /**
     * HTTP PUT /api/counter
     * Java method: de.example.backend.boundary.controller.CounterUiController.update
     */
    update(counterUiDTO: CounterUiDTO, options?: RequestInit): RestResponse<CounterUiDTO> {
        return this.httpClient.request({ method: "PUT", url: uriEncoding`api/counter`, data: counterUiDTO, options: options });
    }

    /**
     * HTTP GET /api/counter/{id}
     * Java method: de.example.backend.boundary.controller.CounterUiController.getById
     */
    getById(id: string, options?: RequestInit): RestResponse<CounterUiDTO> {
        return this.httpClient.request({ method: "GET", url: uriEncoding`api/counter/${id}`, options: options });
    }
}

export class ChatUiControllerClient {

    constructor(protected httpClient: HttpClient) {
    }

    /**
     * HTTP POST /api/chat/
     * Java method: de.example.backend.boundary.controller.ChatUiController.createChat
     */
    createChat(options?: RequestInit): RestResponse<string> {
        return this.httpClient.request({ method: "POST", url: uriEncoding`api/chat/`, options: options });
    }

    /**
     * HTTP POST /api/chat/{chatId}
     * Java method: de.example.backend.boundary.controller.ChatUiController.sendMessage
     */
    sendMessage(chatId: string, request: ChatRequestUiDTO, options?: RequestInit): RestResponse<ChatResponseUiDTO> {
        return this.httpClient.request({ method: "POST", url: uriEncoding`api/chat/${chatId}`, data: request, options: options });
    }
}

export type RestResponse<R> = Promise<R>;

export type ChatResponseStatus = "SUCCESS" | "REFUSAL" | "ERROR";

export type ViewType = "NONE" | "RECIPE";

function uriEncoding(template: TemplateStringsArray, ...substitutions: any[]): string {
    let result = "";
    for (let i = 0; i < substitutions.length; i++) {
        result += template[i];
        result += encodeURIComponent(substitutions[i]);
    }
    result += template[template.length - 1];
    return result;
}
