import type { ReactNode } from "react";
import { App } from "antd";
import {
  CheckCircleTwoTone,
  InfoCircleTwoTone,
  WarningTwoTone,
} from "@ant-design/icons";
import { tokens } from "#/theme/tokens.ts";
import type { ArgsProps } from "antd/es/notification";

export type NotificationProps = Omit<ArgsProps, "type"> & {
  title: ReactNode;
  description?: ReactNode;
};

const commonProps = {
  duration: 3,
  pauseOnHover: true,
  showProgress: true,
};

export const useNotification = () => {
  const { notification } = App.useApp();

  const success = (props: NotificationProps) => {
    notification.success({
      ...commonProps,
      icon: <CheckCircleTwoTone twoToneColor={tokens.color.success} />,
      ...props,
    });
  };

  const info = (props: NotificationProps) => {
    notification.info({
      ...commonProps,
      icon: <InfoCircleTwoTone twoToneColor={tokens.color.info} />,
      ...props,
    });
  };

  const warn = (props: NotificationProps) => {
    notification.info({
      ...commonProps,
      icon: <WarningTwoTone twoToneColor={tokens.color.warning} />,
      ...props,
    });
  };

  const error = (props: NotificationProps) => {
    notification.error({
      ...commonProps,
      icon: <WarningTwoTone twoToneColor={tokens.color.error} />,
      ...props,
    });
  };

  return {
    success,
    info,
    warn,
    error,
  };
};
