import { DeleteOutlined, PlusOutlined } from "@ant-design/icons";
import { tokens } from "../../theme/tokens.ts";
import { Caption, DocsNav, Example, Section } from "./DocsLayout.tsx";
import { blockStyle } from "./docsStyles.ts";
import {
  SectionHeader,
  TitleDivider,
} from "../../common/components/SectionHeader.tsx";
import {
  App,
  Badge,
  Button,
  Collapse,
  DatePicker,
  Empty,
  Flex,
  Form,
  Input,
  InputNumber,
  Select,
  Tag,
  Typography,
} from "antd";

/**
 * Living reference for the basic components: how they are structured and
 * combined in HolidayMate. Complements the token reference on /docs.
 */

const ACTIVITY_OPTIONS = ["Camping", "Kayaking", "Beach"].map((name) => ({
  value: name,
  label: name,
}));

const HeaderSection = () => (
  <Section title="Headers">
    <Example
      title="SectionHeader — page title"
      description="Exactly one per page, as the first element. The title names the page, the description says in one sentence what the user can do here."
      usage='<SectionHeader title="…" description="…" />'
    >
      <SectionHeader
        title="Planning Menu"
        description="Generate a packing list for your next trip."
      />
    </Example>

    <Example
      title="SectionHeader with actions"
      description="Pass any JSX to extra; it is placed next to the title. Use it for page-wide actions or navigation (see the Tokens / Components switch above). Keep it to one or two compact controls in default size."
      usage="<SectionHeader title=… extra={<Button>…</Button>} />"
    >
      <SectionHeader
        title="Planned Holidays"
        description="Your upcoming trips and their packing lists."
        extra={
          <Button type="primary" icon={<PlusOutlined />}>
            Plan
          </Button>
        }
      />
    </Example>

    <Example
      title="TitleDivider — section title"
      description="Structures a page into sections. Use level 5 below a SectionHeader. extra works the same way, e.g. for section actions like an export."
      usage='<TitleDivider title="…" level={5} extra={…} />'
    >
      <TitleDivider
        title="Generated Packing List"
        level={5}
        extra={<Button>PDF</Button>}
      />
    </Example>
  </Section>
);

const InputSection = () => (
  <Section title="Input fields">
    <Example
      title="Form structure"
      description={
        <>
          Wrap inputs in a vertical Form without required marks. Every field is
          a Form.Item with name, visible label and a validation message that
          says what to enter. Inputs in forms use size="large". The placeholder
          shows an example value, never the label again.
        </>
      }
      usage='<Form layout="vertical" requiredMark={false}>'
    >
      <Form layout="vertical" requiredMark={false}>
        <Form.Item
          name="name"
          label="Activity name"
          rules={[
            {
              required: true,
              whitespace: true,
              message: "Enter a name",
            },
          ]}
        >
          <Input size="large" placeholder="Camping trip" />
        </Form.Item>
        <Form.Item name="location" label="Location" style={{ marginBottom: 0 }}>
          <Input size="large" placeholder="Lake Garda" />
        </Form.Item>
      </Form>
    </Example>

    <Example
      title="Validation"
      description="Errors appear below the field in the error color. The message tells the user how to fix it (“Enter a name”), not what went wrong (“Invalid”). Validate on save; open collapsed groups that contain errors."
      usage='rules={[{ required: true, message: "Enter a name" }]}'
    >
      <Form layout="vertical" requiredMark={false}>
        <Form.Item
          label="Activity name"
          validateStatus="error"
          help="Enter a name"
          style={{ marginBottom: 0 }}
        >
          <Input size="large" placeholder="Camping trip" />
        </Form.Item>
      </Form>
    </Example>

    <Example
      title="Field group without labels"
      description="Short groups where the placeholder is self-explanatory may omit labels. Stack them with gap.small and stretch every field to full width."
      usage="<Flex vertical gap={tokens.gap.small}>"
    >
      <Flex vertical gap={tokens.gap.small}>
        <DatePicker.RangePicker
          size="large"
          placeholder={["Start date", "End date"]}
          style={{ width: "100%" }}
        />
        <Input size="large" placeholder="Location" />
        <Select
          size="large"
          mode="multiple"
          placeholder="Activities"
          options={ACTIVITY_OPTIONS}
          style={{ width: "100%" }}
        />
      </Flex>
    </Example>

    <Example
      title="Repeating rows"
      description="For lists of entries (Form.List): the main input takes the remaining space, short numeric inputs use width.numberField, the delete action is a text icon button with aria-label. Rows and the dashed add button are separated by gap.small; Form.Item margins are removed."
      usage='<Flex gap={tokens.gap.small} align="start">'
    >
      <Flex vertical gap={tokens.gap.small}>
        {[
          { name: "Tent", amount: 1 },
          { name: "Sleeping bag", amount: 2 },
        ].map((item) => (
          <Flex key={item.name} gap={tokens.gap.small} align="start">
            <Input size="large" defaultValue={item.name} style={{ flex: 1 }} />
            <InputNumber
              size="large"
              min={1}
              precision={0}
              defaultValue={item.amount}
              aria-label="Amount"
              style={{ width: tokens.width.numberField }}
            />
            <Button
              type="text"
              size="large"
              icon={<DeleteOutlined />}
              aria-label="Remove item"
            />
          </Flex>
        ))}
        <Button type="dashed" size="large" block icon={<PlusOutlined />}>
          Add item
        </Button>
      </Flex>
    </Example>

    <Example
      title="Toolbar inputs"
      description="Inputs that filter or select what the page shows (not form data) use the default size and sit in a row with their action, separated by gap.small. The input takes the remaining space."
      usage="<Flex gap={tokens.gap.small}>"
    >
      <Flex gap={tokens.gap.small}>
        <Select
          placeholder="Select an activity to edit"
          options={ACTIVITY_OPTIONS}
          style={{ flex: 1 }}
        />
        <Button icon={<PlusOutlined />}>New activity</Button>
      </Flex>
    </Example>
  </Section>
);

const ButtonSection = () => (
  <Section title="Buttons">
    <Example
      title="Hierarchy"
      description="primary for the single most important action on a screen, default for secondary actions, dashed for adding entries to a list, text for icon-only row actions. Never place two primary buttons next to each other."
      usage='<Button type="primary">'
    >
      <Flex wrap gap={tokens.gap.small}>
        <Button type="primary">Save activity</Button>
        <Button>Cancel</Button>
        <Button type="dashed" icon={<PlusOutlined />}>
          Add item
        </Button>
        <Button type="text" icon={<DeleteOutlined />} aria-label="Remove" />
      </Flex>
    </Example>

    <Example
      title="Main page action"
      description="The action that completes a page (e.g. saving a form) is a large, full-width primary button in a sticky bar at the bottom, so it stays reachable on long pages."
      usage='<Button type="primary" size="large" block>'
    >
      <div
        style={{
          padding: `${tokens.padding.medium}px 0`,
          background: tokens.color.background.container,
        }}
      >
        <Button type="primary" size="large" block>
          Save activity
        </Button>
      </div>
    </Example>
  </Section>
);

const StructureSection = () => {
  const { message } = App.useApp();

  return (
    <Section title="Structure and feedback">
      <Example
        title="Collapse group"
        description='Groups related fields that can be hidden, e.g. one category of packing items. Use size="large", a strong label in fontSize.large and a count Badge as extra. Stack several groups with gap.large.'
        usage='<Collapse size="large" items={…} />'
      >
        <Collapse
          size="large"
          defaultActiveKey={["clothing"]}
          items={[
            {
              key: "clothing",
              label: (
                <Typography.Text
                  strong
                  style={{ fontSize: tokens.fontSize.large }}
                >
                  Clothing
                </Typography.Text>
              ),
              extra: <Badge count={2} color={tokens.color.primary} />,
              children: <div style={blockStyle}>Group content</div>,
            },
          ]}
        />
      </Example>

      <Example
        title="Empty state"
        description="Shown instead of an empty list. The description explains why nothing is there; if the user can change that, add the fitting primary action. Use the simple image inside sections."
        usage="<Empty description=… />"
      >
        <Flex vertical gap={tokens.gap.large}>
          <Empty description="No holidays planned yet">
            <Button type="primary" icon={<PlusOutlined />}>
              Plan a holiday
            </Button>
          </Empty>
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description="Select activities to generate a packing list"
          />
        </Flex>
      </Example>

      <Example
        title="Messages"
        description="Short confirmation after an action, via App.useApp() so the theme applies. success names what was done, error says what failed and includes the reason."
        usage="const { message } = App.useApp();"
      >
        <Flex wrap gap={tokens.gap.small}>
          <Button onClick={() => message.success('Saved "Camping"')}>
            Success message
          </Button>
          <Button
            onClick={() =>
              message.error("Could not save activity: Network error")
            }
          >
            Error message
          </Button>
        </Flex>
      </Example>

      <Example
        title="Tags"
        description="Read-only labels for categories such as activities. Not clickable and never used for status — use Badge or messages for that. Separate with gap.small."
        usage="<Tag>Camping</Tag>"
      >
        <Flex vertical gap={tokens.gap.small}>
          <Flex wrap gap={tokens.gap.small}>
            {ACTIVITY_OPTIONS.map(({ label }) => (
              <Tag key={label} style={{ marginInlineEnd: 0 }}>
                {label}
              </Tag>
            ))}
          </Flex>
          <Caption>Activities of a planned holiday</Caption>
        </Flex>
      </Example>
    </Section>
  );
};

export const ComponentsDocsPage = () => (
  <Flex vertical style={{ paddingBottom: tokens.padding.large }}>
    <SectionHeader
      title="Components"
      description="How the basic components are structured and combined. Values come from src/theme/tokens.ts — see the Tokens page."
      extra={<DocsNav />}
    />

    <HeaderSection />
    <InputSection />
    <ButtonSection />
    <StructureSection />
  </Flex>
);
