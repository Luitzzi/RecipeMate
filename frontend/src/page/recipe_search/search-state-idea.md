# Search state idea

## Current state
The split between `RecipeFilterUiDTO` and `RecipeSearchFormValues` is clean: it ensures no wrong values reach the backend.

## Problem
Other pages (e.g. folders) need to open the search page with prefilled values.
With the current split this requires:
1. passing a `RecipeFilterUiDTO` as navigation state,
2. converting it back to `RecipeSearchFormValues` (a second mapper),
3. inserting it into the form.

This is neither clean nor simple.

## Proposal
Rename `RecipeSearchFormValues` → `RecipeSearchFilterState`.

The search form and other pages then share **one input** into the search: `RecipeSearchFilterState`.

## Example

`recipeSearchFormUtils.ts`
```ts
export type RecipeSearchFilterState = {
  title?: string;
  language: Language;
  // ...
  folderId?: number; // changed: search has to work with id instead of name
  status: Status; // new: opens/collapses the filter inputs
};

export const defaultFilterState = (): RecipeSearchFilterState => ({
  language: Language.DE,
  status: Status.Open,
});

export const createFilterState = (partial: Partial<RecipeSearchFilterState> = {}): RecipeSearchFilterState => ({
  ...defaultFilterState(),
  ...partial,
});
```

`RecipeSearchPage.tsx`
```tsx
const passedState: Partial<RecipeSearchFilterState> = useLocation().state?.filterState ?? {};
const [filterState, setRecipeSearchFilterState] = useState(() => createFilterState(passedState));
```

`FolderPage.tsx`
```tsx
navigate("/recipe-search", {
  state: { filterState: { folderId: folder.id, status: Status.Closed } },
});
```
