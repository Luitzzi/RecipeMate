# Design system

## Style token definition
The design tokens are defined in /frontend/src/theme/tokens.ts.
The overview over the design tokens and the system is in /frontend/design-docs/design-tokens.md.
Always import tokens and then get the style value from there.

```ts
import {tokens} from "../../theme/tokens.ts";

<ExampleComponent style={{marginTop: tokens.margin.small}} />
```

Only define your own values when it is one-time-use.
Follow Ant Design's design principles described on their [website](https://ant.design/docs/spec/introduce)
and in /frontend/design-docs/antd-docs/ant-d-design-system.md.

## Ant Design documentation
For agents the antd documentation is in /frontend/design-docs/antd-docs.
For humans read the [design principles](https://ant.design/docs/spec/introduce) and
the [predefined components](https://ant.design/components/overview/) on Ant Design's website.

