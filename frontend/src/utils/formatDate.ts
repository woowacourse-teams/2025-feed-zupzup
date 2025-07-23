export default function formatDate(date: string) {
  // 입력값 = "2023-10-01T12:00:00Z"
  const spliceDate = date.split('T');
  const datePart = spliceDate[0];
  return datePart;
}
