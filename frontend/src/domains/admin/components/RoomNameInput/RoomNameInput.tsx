import Input from '@/components/@commons/Input/Input';
import { roomNameInput } from '@/domains/admin/components/RoomNameInput/RoomNameInput.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface RoomNameInputProps {
  roomName: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function RoomNameInput({
  roomName,
  onChange,
}: RoomNameInputProps) {
  const theme = useAppTheme();

  return (
    <div>
      <p>방 이름</p>
      <Input
        value={roomName}
        onChange={onChange}
        maxLength={20}
        minLength={1}
        placeholder='방 이름을 입력하세요.'
        customCSS={roomNameInput(theme)}
      />
    </div>
  );
}
