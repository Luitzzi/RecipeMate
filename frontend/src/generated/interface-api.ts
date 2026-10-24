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

export interface CurrentViewUiDTO {
    type: ViewType;
    id?: string;
}

export interface IngredientUiDTO {
    id?: number;
    name: string;
    type?: string;
}

export interface OvenStepUiDTO extends RecipeStepUiDTO {
    stepMethod: "OVEN";
    temperatureC: number;
    ovenMode: OvenMode;
    durationSeconds: number;
    preheat: boolean;
}

export interface PrepareStepUiDTO extends RecipeStepUiDTO {
    stepMethod: "PREPARE";
}

export interface RecipeCuisineUiDTO {
    id: number;
    name: string;
}

export interface RecipeIngredientUiDTO {
    ingredient: IngredientUiDTO;
    amount?: number;
    unit: Unit;
}

export interface RecipePropertyUiDTO {
    id: number;
    name: string;
}

export interface RecipeStepUiDTO {
    stepMethod: "OVEN" | "PREPARE" | "STOVE";
    id?: number;
    description: string;
}

export interface RecipeUiDTO {
    id?: number;
    title: string;
    description: string;
    cookTimeInMinutes: number;
    difficulty: number;
    diet: Diet;
    note?: string;
    sourceUrl?: string;
    ingredients: RecipeIngredientUiDTO[];
    steps: RecipeStepUiDTOUnion[];
    cuisines: RecipeCuisineUiDTO[];
    properties: RecipePropertyUiDTO[];
    imageKeys: string[];
}

export interface StoveStepUiDTO extends RecipeStepUiDTO {
    stepMethod: "STOVE";
    heatLevel: HeatLevel;
}

export interface HttpClient {

    request<R>(requestConfig: { method: string; url: string; queryParams?: any; data?: any; copyFn?: (data: R) => R; options?: RequestInit; }): RestResponse<R>;
}

export class RecipeUiControllerClient {

    constructor(protected httpClient: HttpClient) {
    }

    /**
     * HTTP POST /api/recipies
     * Java method: de.example.backend.boundary.controller.RecipeUiController.create
     */
    create(recipeUiDTO: RecipeUiDTO, options?: RequestInit): RestResponse<RecipeUiDTO> {
        return this.httpClient.request({ method: "POST", url: uriEncoding`api/recipies`, data: recipeUiDTO, options: options });
    }

    /**
     * HTTP GET /api/recipies
     * Java method: de.example.backend.boundary.controller.RecipeUiController.getAll
     */
    getAll(queryParams: { language: Language; }, options?: RequestInit): RestResponse<RecipeUiDTO[]> {
        return this.httpClient.request({ method: "GET", url: uriEncoding`api/recipies`, queryParams: queryParams, options: options });
    }

    /**
     * HTTP GET /api/recipies/{id}
     * Java method: de.example.backend.boundary.controller.RecipeUiController.get
     */
    get(id: number, queryParams: { language: Language; }, options?: RequestInit): RestResponse<RecipeUiDTO> {
        return this.httpClient.request({ method: "GET", url: uriEncoding`api/recipies/${id}`, queryParams: queryParams, options: options });
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

export type Language = "DE" | "EN";

export type ChatResponseStatus = "SUCCESS" | "REFUSAL" | "ERROR";

export type ViewType = "NONE" | "RECIPE";

export type OvenMode = "TOP_BOTTOM_HEAT" | "CIRCULATING_AIR";

export type Unit = "GRAM" | "KILOGRAM" | "MILLILITER" | "LITER" | "TEASPOON" | "TABLESPOON" | "CUP" | "PIECE" | "CLOVE" | "SLICE" | "BUNCH" | "SPRING" | "LEAF" | "CAN" | "PACKAGE" | "CUP_CONTAINER" | "CUBE" | "PINCH" | "KNIFE_TIP" | "DASH" | "TO_TASTE";

export type Diet = "VEGAN" | "VEGETARIAN" | "PESCATARIAN" | "OMNIVORE";

export type HeatLevel = "LOW" | "MEDIUM" | "HIGH";

export type RecipeStepUiDTOUnion = PrepareStepUiDTO | OvenStepUiDTO | StoveStepUiDTO;

function uriEncoding(template: TemplateStringsArray, ...substitutions: any[]): string {
    let result = "";
    for (let i = 0; i < substitutions.length; i++) {
        result += template[i];
        result += encodeURIComponent(substitutions[i]);
    }
    result += template[template.length - 1];
    return result;
}
