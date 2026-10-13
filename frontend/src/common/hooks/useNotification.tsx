import type { ReactNode } from "react";
import { App } from "antd";
import {
  CheckCircleTwoTone,
  InfoCircleTwoTone,
  WarningTwoTone,
} from "@ant-design/icons";
import type { ArgsProps } from "antd/lib/notification";
import { tokens } from "#/theme/tokens.ts";

export type NotificationProps = {
  title: ReactNode;
  description?: ReactNode;
  props?: ArgsProps;
};

const commonProps = {
  duration: 3,
  pauseOnHover: true,
  showProgress: true,
};

export const useNotification = () => {
  const { notification } = App.useApp();

  const success = ({ title, description, props }: NotificationProps) => {
    notification.success({
      title,
      description,
      ...commonProps,
      icon: <CheckCircleTwoTone twoToneColor={tokens.color.success} />,
      ...props,
    });
  };

  const info = ({ title, description, props }: NotificationProps) => {
    notification.info({
      title,
      description,
      ...commonProps,
      icon: <InfoCircleTwoTone twoToneColor={tokens.color.info} />,
      ...props,
    });
  };

  const warn = ({ title, description, props }: NotificationProps) => {
    notification.info({
      title,
      description,
      ...commonProps,
      icon: <WarningTwoTone twoToneColor={tokens.color.warning} />,
      ...props,
    });
  };

  const error = ({ title, description, props }: NotificationProps) => {
    notification.error({
      title,
      description,
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
