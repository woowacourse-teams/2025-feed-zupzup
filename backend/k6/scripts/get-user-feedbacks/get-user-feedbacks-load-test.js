import http from 'k6/http';
import {check, sleep} from 'k6';
import {Rate} from 'k6/metrics';
import {PROCESS_STATUS, SORT_OPTIONS} from '../../utils/config.js';
import {BASE_URL} from '../../utils/secret.js';

// 에러율 추적을 위한 커스텀 메트릭
export const errorRate = new Rate('errors');

export const options = {
  stages: [
    {duration: '1m', target: 200},
    {duration: '4m', target: 200}
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내
    http_req_failed: ['rate<0.1'],     // 실패율 10% 미만
    errors: ['rate<0.1'],              // 에러율 10% 미만
  },
};

// 테스트용 단체 UUID 목록
const ORGANIZATION_UUIDS = [
  '87748a18-e234-49f9-a72c-d40ca5e702ad',
  '19685572-b633-41ae-b6c0-de641cff1fc3',
  '408aced1-e8f8-42ee-a5d0-5069027279bc',
  'a47c7c57-98a4-4e03-99f1-34108b74e785',
  '0c63bc73-6878-4ed8-a665-899421c259e1',
  'b759791c-2d24-4b20-bcb5-1ec8f61449d2',
  '4cf16712-2d1c-46fc-9983-f1d026d02e82',
  '560ca9eb-8677-4829-bdd5-da179669c7a3',
  'd7568821-3d1f-4bbc-98fc-87e1bd0fe50d',
  'da3ff52b-5ad9-4469-bed8-d0980ede482f',
  '06a0747f-619a-456f-9e93-8b91e5d11f2f',
  '190105fe-366d-4e44-9860-e91f40a9c6cb',
  'ba51c2dd-e95d-4fc0-a211-fc4f85be19aa',
  'a7c14694-6770-453e-b1f0-7f0e7a984e7f',
  'fbbc731b-51aa-4575-b206-bd1acac5fde6',
  'ec5d7cd6-ed6f-4ef5-a630-2bf69b756197',
  '19afc170-18dd-4391-9e30-2f11f8d69468',
  '2fdfa7ec-404e-49ac-b053-877d5f7cda12',
  '6ab8c520-9481-462e-b0eb-87aa121e70d6',
  'e88c052b-256d-4d0c-aea2-26672837230b',
  '2cdd12ad-1e37-4053-bf73-90783c3b2871',
  'c88f222e-2fa5-4b3a-91c8-51123a8c7067',
  'acafe454-3149-4e80-8033-0c9f416bb897',
  '8252e54f-308e-4fdc-ab30-39024ea707e8',
  'e05ff3a9-767d-44dc-ae5b-74d341dda4f4',
  'edcd0656-f898-4468-955e-e573b0380e7b',
  'b02f38f9-0765-49ea-88df-fc92551fb508',
  '1640ad83-7f0f-4ea2-8b1e-bd2f78fe3322',
  '43f5ebe6-df64-4d96-8f7a-8dbcb21acbcf',
  '412399ef-8409-4091-8562-f2c362342733',
  'dc77a050-23bb-4aa5-af6a-88c3a28073ad',
  'f9b17430-3247-45b7-ba22-726f30e98906',
  '26256931-ebe0-453f-9c5f-6b6f03a7844d',
  '8f93b3f0-6182-4e92-9856-d128496fc060',
  'ed679bc4-4415-444f-98b4-fe79452b4e18',
  'b160d45f-a78a-4265-8c7d-dee613d0d5e4',
  'b6308671-76d2-4b6c-8156-49cb16250af8',
  '3a7a349f-41a4-40e3-8bd8-303d922015d4',
  'a0283f64-a261-4509-afac-50efefecac5e',
  '3bded76a-8c7c-4198-a6e7-66cf1469fee4',
  '0edb1df7-211a-43e3-a27c-262aa03e6161',
  '2c53bc15-b3b4-4ac7-8644-814481b55aa0',
  'e78d97b2-364c-4a47-a5e7-440441d01672',
  'ba8bd648-9401-4777-b781-b8ca8383c6d9',
  '300b38c5-bfe1-4e90-ac32-021ee2a5cd98',
  '39ab73a0-3310-46a0-9ab7-87c0bed77de9',
  '84ee03cd-9ec5-4c6a-bb3f-41052abaef9b',
  'b308ad67-0392-4f5f-a162-c6d37ead14b9',
  '78dc420e-81cf-4fcb-8cb9-bdbab2eac4cb',
  '0f13b80d-a649-48af-8281-a0a5765bbf7b',
  '76fda965-4c46-402d-afe5-4e83282d5d6d',
  'f10ec142-48c8-4339-90f3-ada7a1e5a4e4',
  '27ceab01-2a78-433e-9043-35e2bc3d4fd3',
  '5ba20183-c0c7-47a9-a7dd-2683135a054d',
  'b7653c47-a887-4867-961b-a5146de8fcf4',
  '738c71c2-40d7-48b0-aafe-af25664753bc',
  'd942003b-67a6-43d6-bf55-ad92bc3d9a17',
  '83953928-8319-4339-8847-96c286e1f9e4',
  '26d0739c-f0cc-4f41-82ad-dac334e2ceaa',
  '2c446732-3915-4d5a-9d4c-0649c6a6f819',
  'bbf3138e-f0ae-4030-bd28-13a8fe6e83a0',
  '71f5aa7c-05cf-4ddd-8596-07a063567c74',
  '4c997c8b-09c8-4c1d-abc5-8ad763f4994a',
  '6525c3ee-d9d4-44a4-b2d6-8f611ace52d4',
  '9b6bd03d-72f9-4534-8561-4c5b0c6badf3',
  '4e48ee67-c4da-440c-a4b1-fdc51bdac2ff',
  'ddb8d2a4-7c92-4772-89ce-43606690dd7a',
  '0a8c49c1-eb97-40d7-b2f7-f5ec81dad20d',
  'c59273b9-4a7b-4afd-b285-61534b7144be',
  'ecf404e5-5878-4358-a91b-a9755d196717',
  'c76b3c5e-2e0f-4ea8-95eb-834d0e6a0ec5',
  '615ebcad-7b6f-4080-be44-2d712c3e4c1e',
  '712fae26-499e-456c-8b00-a9b55525b937',
  '08b52922-7829-4246-8183-a1f2466b2556',
  '39ffd90c-d815-4fcf-80d9-9d4055648d2f',
  '9b40e9db-317a-41c1-a212-bd616d8f57d1',
  'f92c7ad6-cee0-4ea9-9d77-9df11e7256d7',
  'd019f39c-ab39-4e30-b3f4-7ce206511cfc',
  '0bd8eec4-92d6-49b0-bcaf-1c3348ffc754',
  'e4b313b9-87db-488b-8214-fcfc9dc605d6',
  'ddac8560-f454-4b9c-a56e-67c318a6cf1c',
  '8841d642-1c6e-41d9-b8a6-ee2f3dcce0c2',
  '86d1e844-fce7-48ce-ab0e-f6fa47d0dd39',
  '19499d94-354f-43f0-af12-e4dcb0d91cd5',
  '85345211-471f-4be0-b841-714dfd8326ac',
  '039cccf8-009a-4b46-af55-f06bd94bc513',
  '34076745-cc2d-4e47-9301-e383f089c7fd',
  '8ffbb9b9-d445-4cb9-89f4-87c0adc48c5c',
  'cb9bc3fe-6493-4c01-8200-524b44c50625',
  'c1957a05-9c03-4f49-8364-78399481dec0',
  '8e3e81b9-3fb3-4582-b384-180d271ce0e6',
  'afeed0d0-3607-4501-aa25-9b3dee0ada6d',
  'ad07dca9-0958-4fd6-87e8-1405fd7b57fb',
  '8d0b9a89-ac0e-4cfa-9764-ca97a2337cfb',
  'dd3d7b00-a297-405b-bc74-cb10fba762ec',
  '2d9e58e3-1a61-4859-b30a-da89dbdf9721',
  '0000b3f8-dca9-40e3-9a84-2765f3f661e2',
  'a6300926-2b93-4296-82f0-b260190dc44a',
  'dd54de73-16f3-44cc-9bea-eca341257def',
  '9ad01be0-0c56-4f3e-99fc-635bd6942dbf',
  '9310ae05-c75a-4d71-806d-f0943ba001e6',
  'bd5fa0f0-02e0-4e65-bad5-8779aa7eca98',
  'faa75651-2e75-4d64-8c62-eebfb37231db',
  'a76cbdf7-53c1-4efb-9b41-268082505353',
  '194d94a0-8fa5-466b-9857-c2a5f8bc7e23',
  'ac72be1f-faa7-4993-a02c-971112561a7d',
  '137242f3-5184-4ff7-9a61-7733b93c9fab',
  '1cd3f69f-56bb-4f0a-a099-7f936c855a33',
  '53cb3729-a731-488e-9c1b-b723bb18dfe2',
  'f4ebc02a-afa2-49d5-be8f-ce5a4e102e86',
  '121fb76c-ee90-4c16-818a-2c133560341e',
  'ad56bc1c-3437-48b9-8dd1-98159b26bb96',
  '37f7e6cf-7a2c-44f8-aaa5-2605a6eb9f4b',
  '0084888e-cce2-4334-b471-fc3daf01a019',
  'd10e4f9a-bb96-4ec0-b3df-7c438373e878',
  'c003534f-5323-4090-b30f-953f631410b3',
  'c513734f-c1b9-4bcd-bf20-68a346cc1684',
  '3a196128-9042-4084-bd23-d0f2c0878879',
  '8dab395a-0679-477f-b07c-9833986288b2',
  '2b949fd4-edce-4e0f-b1b3-4885e77f61d9',
  '66f29667-dd7b-4958-9ecb-d4d9be42ae22',
  'fa5750e3-497d-4010-95ae-0c13ed8c455f',
  'e131eb4a-64a1-4a1e-83dd-380235dd045f',
  'e8d25a3b-2be9-403e-97ab-048a618ed15b',
  '5390fefd-9b45-4fa7-a0d5-40216d739867',
  '028f5b30-b11a-4dee-9e05-66feaf569838',
  '7a0967ed-f04f-4f8a-ad48-46cdf1ab1749',
  '65bf3d43-f185-490c-acad-7f6cc2ea254a',
  '90f8fb90-da29-4566-95ca-399450fb4070',
  '0b17eff2-a6ca-4dec-8c31-9fbca5989fc5',
  '6097f01a-fa0f-455d-ba19-d140de97725d',
  '7655942b-7a91-4333-91cd-bfd44f790ba7',
  '358becee-5394-4c44-8cf6-5b0a3b6be0fb',
  '47f2fc43-e20b-47d5-93a8-71c91248f150',
  '86733b91-5314-4482-bbc4-fd9805e0bab0',
  'd0915e35-2711-4cbf-a9f8-fd5f6d26fe0d',
  'b8d81831-428b-429b-adf1-00827b270809',
  '3bde3289-53cf-40d6-bd29-104d56c8e537',
  'aece4294-f557-48dc-815a-c40e4016c26c',
  '33c6de05-6822-4f9a-a6fa-5a1e758f3953',
  '03b88884-f35d-4c2e-a091-b029289014a8',
  'e8b804eb-c29b-47ac-bb9e-47704d572464',
  '55e848ae-01ae-4bb7-9058-4efdceea95e3',
  '59e26c47-436e-4d58-953b-2bdcaf032190',
  'dca6a21a-4374-49ce-96c5-81cef89b43ab',
  '168db591-16e0-4f09-a4f8-46ddf93a536e',
  '41548aee-2083-43ab-9ecb-c6e5f2a47c2f',
  '69704d8e-1874-4fdd-bbe5-4e93cfd3894a',
  '2dae6d20-8530-4baf-aba7-03d7e1b2947a',
  'c445d7af-0d02-4bba-a2ed-312c4dd5e3d2'
]

export default function () {
  // 랜덤 조직 UUID 선택
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  // 랜덤 파라미터 생성
  const size = 10;
  const sortBy = SORT_OPTIONS[Math.floor(Math.random() * SORT_OPTIONS.length)];
  const status = Math.random() > 0.5 ? PROCESS_STATUS[Math.floor(
      Math.random() * PROCESS_STATUS.length)] : '';
  const cursorId = Math.random() > 0.7 ? Math.floor(Math.random() * 4000000) + 1
      : '';

  // 쿼리 파라미터 구성
  let queryParams = `size=${size}&sortBy=${sortBy}`;
  if (status) {
    queryParams += `&status=${status}`;
  }
  if (cursorId) {
    queryParams += `&cursorId=${cursorId}`;
  }

  const url = `${BASE_URL}/organizations/${orgUuid}/feedbacks?${queryParams}`;

  // API 요청
  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  });

  // 응답 검증
  const isSuccess = check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
    'response has success field': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.hasOwnProperty('success');
      } catch {
        return false;
      }
    },
    'response success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'response has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.hasOwnProperty('feedbacks');
      } catch {
        return false;
      }
    },
  });

  // 에러율 기록
  errorRate.add(!isSuccess);

  // 요청 간격 (1초)
  sleep(1);
}
