import { Meta, StoryObj } from '@storybook/react-webpack5';
import NotFoundPage from './NotFoundPage';
import { MemoryRouter } from 'react-router-dom';

const meta: Meta<typeof NotFoundPage> = {
  title: 'Components/NotFoundPage',
  component: NotFoundPage,
  parameters: {
    layout: 'fullscreen',
  },
  decorators: [
    (Story) => (
      <MemoryRouter>
        <div style={{ height: '100vh' }}>
          <Story />
        </div>
      </MemoryRouter>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof NotFoundPage>;

export const Default: Story = {};
