import DashboardPanel from '@/domains/dashboard/components/DashboardPanel/DashboardPanel';

const GROUP_NAME = '우아한테크코스';

export default function Dashboard() {
  return (
    <div>
      <p>{GROUP_NAME}</p>
      <div>
        <p>일주일 간의 피드백</p>
        <div>
          <DashboardPanel title='반영률' content='40%' caption='총 2개 반영' />
        </div>
      </div>
    </div>
  );
}
