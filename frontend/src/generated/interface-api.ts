/* eslint-disable */
// @ts-nocheck

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

export interface ShoppingListItemUiDTO {
    id?: number;
    ingredient?: IngredientUiDTO;
    name?: string;
    amount?: number;
    unit?: Unit;
    checked: boolean;
}

export interface ShoppingListUiDTO {
    id: number;
    items: ShoppingListItemUiDTO[];
}

export interface StoveStepUiDTO extends RecipeStepUiDTO {
    stepMethod: "STOVE";
    heatLevel: HeatLevel;
}

export interface ImageUrlUiDTO {
    key: string;
    url: URL;
}

export interface URL extends Serializable {
}

export interface Serializable {
}

export interface HttpClient {

    request<R>(requestConfig: { method: string; url: string; queryParams?: any; data?: any; copyFn?: (data: R) => R; options?: RequestInit; }): RestResponse<R>;
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

export class ShoppingListUiControllerClient {

    constructor(protected httpClient: HttpClient) {
    }

    /**
     * HTTP GET /api/shopping-list
     * Java method: de.example.backend.boundary.controller.ShoppingListUiController.get
     */
    get(queryParams: { language: Language; }, options?: RequestInit): RestResponse<ShoppingListUiDTO> {
        return this.httpClient.request({ method: "GET", url: uriEncoding`api/shopping-list`, queryParams: queryParams, options: options });
    }

    /**
     * HTTP POST /api/shopping-list/items
     * Java method: de.example.backend.boundary.controller.ShoppingListUiController.addItem
     */
    addItem(item: ShoppingListItemUiDTO, queryParams: { language: Language; }, options?: RequestInit): RestResponse<ShoppingListUiDTO> {
        return this.httpClient.request({ method: "POST", url: uriEncoding`api/shopping-list/items`, queryParams: queryParams, data: item, options: options });
    }

    /**
     * HTTP DELETE /api/shopping-list/items/{itemId}
     * Java method: de.example.backend.boundary.controller.ShoppingListUiController.removeItem
     */
    removeItem(itemId: number, queryParams: { language: Language; }, options?: RequestInit): RestResponse<ShoppingListUiDTO> {
        return this.httpClient.request({ method: "DELETE", url: uriEncoding`api/shopping-list/items/${itemId}`, queryParams: queryParams, options: options });
    }

    /**
     * HTTP PATCH /api/shopping-list/items/{itemId}
     * Java method: de.example.backend.boundary.controller.ShoppingListUiController.setItemChecked
     */
    setItemChecked(itemId: number, queryParams: { checked: boolean; language: Language; }, options?: RequestInit): RestResponse<ShoppingListUiDTO> {
        return this.httpClient.request({ method: "PATCH", url: uriEncoding`api/shopping-list/items/${itemId}`, queryParams: queryParams, options: options });
    }
}

export class ImageUiControllerClient {

    constructor(protected httpClient: HttpClient) {
    }

    /**
     * HTTP GET /images/downloadUrl
     * Java method: de.example.backend.boundary.controller.ImageUiController.getDownloadUrl
     */
    getDownloadUrl(queryParams: { key: string; }, options?: RequestInit): RestResponse<ImageUrlUiDTO> {
        return this.httpClient.request({ method: "GET", url: uriEncoding`images/downloadUrl`, queryParams: queryParams, options: options });
    }

    /**
     * HTTP GET /images/uploadUrl
     * Java method: de.example.backend.boundary.controller.ImageUiController.getUploadUrl
     */
    getUploadUrl(queryParams: { contentType: string; }, options?: RequestInit): RestResponse<ImageUrlUiDTO> {
        return this.httpClient.request({ method: "GET", url: uriEncoding`images/uploadUrl`, queryParams: queryParams, options: options });
    }
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

export type RestResponse<R> = Promise<R>;

export type RecipeStepUiDTOUnion = PrepareStepUiDTO | OvenStepUiDTO | StoveStepUiDTO;

export const enum Language {
    DE = "DE",
    EN = "EN",
}

export const enum ChatResponseStatus {
    SUCCESS = "SUCCESS",
    REFUSAL = "REFUSAL",
    ERROR = "ERROR",
}

export const enum ViewType {
    NONE = "NONE",
    RECIPE = "RECIPE",
}

export const enum OvenMode {
    TOP_BOTTOM_HEAT = "TOP_BOTTOM_HEAT",
    CIRCULATING_AIR = "CIRCULATING_AIR",
}

export const enum Unit {
    GRAM = "GRAM",
    KILOGRAM = "KILOGRAM",
    MILLILITER = "MILLILITER",
    LITER = "LITER",
    TEASPOON = "TEASPOON",
    TABLESPOON = "TABLESPOON",
    CUP = "CUP",
    PIECE = "PIECE",
    CLOVE = "CLOVE",
    SLICE = "SLICE",
    BUNCH = "BUNCH",
    SPRING = "SPRING",
    LEAF = "LEAF",
    CAN = "CAN",
    PACKAGE = "PACKAGE",
    CUP_CONTAINER = "CUP_CONTAINER",
    CUBE = "CUBE",
    PINCH = "PINCH",
    KNIFE_TIP = "KNIFE_TIP",
    DASH = "DASH",
    TO_TASTE = "TO_TASTE",
}

export const enum Diet {
    VEGAN = "VEGAN",
    VEGETARIAN = "VEGETARIAN",
    PESCATARIAN = "PESCATARIAN",
    OMNIVORE = "OMNIVORE",
}

export const enum HeatLevel {
    LOW = "LOW",
    MEDIUM = "MEDIUM",
    HIGH = "HIGH",
}

function uriEncoding(template: TemplateStringsArray, ...substitutions: any[]): string {
    let result = "";
    for (let i = 0; i < substitutions.length; i++) {
        result += template[i];
        result += encodeURIComponent(substitutions[i]);
    }
    result += template[template.length - 1];
    return result;
}
