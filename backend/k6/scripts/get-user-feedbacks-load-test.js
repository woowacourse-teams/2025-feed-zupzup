import http from 'k6/http';
import {check, sleep} from 'k6';
import {Rate} from 'k6/metrics';
import {BASE_URL, PROCESS_STATUS, SORT_OPTIONS} from '../utils/config.js';

// 에러율 추적을 위한 커스텀 메트릭
export const errorRate = new Rate('errors');

export const options = {
  stages: [
    {duration: '5m', target: 150},   // 5분 동안 150명까지 증가
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내
    http_req_failed: ['rate<0.1'],     // 실패율 10% 미만
    errors: ['rate<0.1'],              // 에러율 10% 미만
  },
};

// 테스트용 단체 UUID 목록
const ORGANIZATION_UUIDS = [
  '87748a18e23449f9a72cd40ca5e702ad',
  '19685572b63341aeb6c0de641cff1fc3',
  '408aced1e8f842eea5d05069027279bc',
  'a47c7c5798a44e0399f134108b74e785',
  '0c63bc7368784ed8a665899421c259e1',
  'b759791c2d244b20bcb51ec8f61449d2',
  '4cf167122d1c46fc9983f1d026d02e82',
  '560ca9eb86774829bdd5da179669c7a3',
  'd75688213d1f4bbc98fc87e1bd0fe50d',
  'da3ff52b5ad94469bed8d0980ede482f',
  '06a0747f619a456f9e938b91e5d11f2f',
  '190105fe366d4e449860e91f40a9c6cb',
  'ba51c2dde95d4fc0a211fc4f85be19aa',
  'a7c146946770453eb1f07f0e7a984e7f',
  'fbbc731b51aa4575b206bd1acac5fde6',
  'ec5d7cd6ed6f4ef5a6302bf69b756197',
  '19afc17018dd43919e302f11f8d69468',
  '2fdfa7ec404e49acb053877d5f7cda12',
  '6ab8c5209481462eb0eb87aa121e70d6',
  'e88c052b256d4d0caea226672837230b',
  '2cdd12ad1e374053bf7390783c3b2871',
  'c88f222e2fa54b3a91c851123a8c7067',
  'acafe45431494e8080330c9f416bb897',
  '8252e54f308e4fdcab3039024ea707e8',
  'e05ff3a9767d44dcae5b74d341dda4f4',
  'edcd0656f8984468955ee573b0380e7b',
  'b02f38f9076549ea88dffc92551fb508',
  '1640ad837f0f4ea28b1ebd2f78fe3322',
  '43f5ebe6df644d968f7a8dbcb21acbcf',
  '412399ef840940918562f2c362342733',
  'dc77a05023bb4aa5af6a88c3a28073ad',
  'f9b17430324745b7ba22726f30e98906',
  '26256931ebe0453f9c5f6b6f03a7844d',
  '8f93b3f061824e929856d128496fc060',
  'ed679bc44415444f98b4fe79452b4e18',
  'b160d45fa78a42658c7ddee613d0d5e4',
  'b630867176d24b6c815649cb16250af8',
  '3a7a349f41a440e38bd8303d922015d4',
  'a0283f64a2614509afac50efefecac5e',
  '3bded76a8c7c4198a6e766cf1469fee4',
  '0edb1df7211a43e3a27c262aa03e6161',
  '2c53bc15b3b44ac78644814481b55aa0',
  'e78d97b2364c4a47a5e7440441d01672',
  'ba8bd64894014777b781b8ca8383c6d9',
  '300b38c5bfe14e90ac32021ee2a5cd98',
  '39ab73a0331046a09ab787c0bed77de9',
  '84ee03cd9ec54c6abb3f41052abaef9b',
  'b308ad6703924f5fa162c6d37ead14b9',
  '78dc420e81cf4fcb8cb9bdbab2eac4cb',
  '0f13b80da64948af8281a0a5765bbf7b',
  '76fda9654c46402dafe54e83282d5d6d',
  'f10ec14248c8433990f3ada7a1e5a4e4',
  '27ceab012a78433e904335e2bc3d4fd3',
  '5ba20183c0c747a9a7dd2683135a054d',
  'b7653c47a8874867961ba5146de8fcf4',
  '738c71c240d748b0aafeaf25664753bc',
  'd942003b67a643d6bf55ad92bc3d9a17',
  '8395392883194339884796c286e1f9e4',
  '26d0739cf0cc4f4182addac334e2ceaa',
  '2c44673239154d5a9d4c0649c6a6f819',
  'bbf3138ef0ae4030bd2813a8fe6e83a0',
  '71f5aa7c05cf4ddd859607a063567c74',
  '4c997c8b09c84c1dabc58ad763f4994a',
  '6525c3eed9d444a4b2d68f611ace52d4',
  '9b6bd03d72f9453485614c5b0c6badf3',
  '4e48ee67c4da440ca4b1fdc51bdac2ff',
  'ddb8d2a47c92477289ce43606690dd7a',
  '0a8c49c1eb9740d7b2f7f5ec81dad20d',
  'c59273b94a7b4afdb28561534b7144be',
  'ecf404e558784358a91ba9755d196717',
  'c76b3c5e2e0f4ea895eb834d0e6a0ec5',
  '615ebcad7b6f4080be442d712c3e4c1e',
  '712fae26499e456c8b00a9b55525b937',
  '08b52922782942468183a1f2466b2556',
  '39ffd90cd8154fcf80d99d4055648d2f',
  '9b40e9db317a41c1a212bd616d8f57d1',
  'f92c7ad6cee04ea99d779df11e7256d7',
  'd019f39cab394e30b3f47ce206511cfc',
  '0bd8eec492d649b0bcaf1c3348ffc754',
  'e4b313b987db488b8214fcfc9dc605d6',
  'ddac8560f4544b9ca56e67c318a6cf1c',
  '8841d6421c6e41d9b8a6ee2f3dcce0c2',
  '86d1e844fce748ceab0ef6fa47d0dd39',
  '19499d94354f43f0af12e4dcb0d91cd5',
  '85345211471f4be0b841714dfd8326ac',
  '039cccf8009a4b46af55f06bd94bc513',
  '34076745cc2d4e479301e383f089c7fd',
  '8ffbb9b9d4454cb989f487c0adc48c5c',
  'cb9bc3fe64934c018200524b44c50625',
  'c1957a059c034f49836478399481dec0',
  '8e3e81b93fb34582b384180d271ce0e6',
  'afeed0d036074501aa259b3dee0ada6d',
  'ad07dca909584fd687e81405fd7b57fb',
  '8d0b9a89ac0e4cfa9764ca97a2337cfb',
  'dd3d7b00a297405bbc74cb10fba762ec',
  '2d9e58e31a614859b30ada89dbdf9721',
  '0000b3f8dca940e39a842765f3f661e2',
  'a63009262b93429682f0b260190dc44a',
  'dd54de7316f344cc9beaeca341257def',
  '9ad01be00c564f3e99fc635bd6942dbf',
  '9310ae05c75a4d71806df0943ba001e6',
  'bd5fa0f002e04e65bad58779aa7eca98',
  'faa756512e754d648c62eebfb37231db',
  'a76cbdf753c14efb9b41268082505353',
  '194d94a08fa5466b9857c2a5f8bc7e23',
  'ac72be1ffaa74993a02c971112561a7d',
  '137242f351844ff79a617733b93c9fab',
  '1cd3f69f56bb4f0aa0997f936c855a33',
  '53cb3729a731488e9c1bb723bb18dfe2',
  'f4ebc02aafa249d5be8fce5a4e102e86',
  '121fb76cee904c16818a2c133560341e',
  'ad56bc1c343748b98dd198159b26bb96',
  '37f7e6cf7a2c44f8aaa52605a6eb9f4b',
  '0084888ecce24334b471fc3daf01a019',
  'd10e4f9abb964ec0b3df7c438373e878',
  'c003534f53234090b30f953f631410b3',
  'c513734fc1b94bcdbf2068a346cc1684',
  '3a19612890424084bd23d0f2c0878879',
  '8dab395a0679477fb07c9833986288b2',
  '2b949fd4edce4e0fb1b34885e77f61d9',
  '66f29667dd7b49589ecbd4d9be42ae22',
  'fa5750e3497d401095ae0c13ed8c455f',
  'e131eb4a64a14a1e83dd380235dd045f',
  'e8d25a3b2be9403e97ab048a618ed15b',
  '5390fefd9b454fa7a0d540216d739867',
  '028f5b30b11a4dee9e0566feaf569838',
  '7a0967edf04f4f8aad4846cdf1ab1749',
  '65bf3d43f185490cacad7f6cc2ea254a',
  '90f8fb90da29456695ca399450fb4070',
  '0b17eff2a6ca4dec8c319fbca5989fc5',
  '6097f01afa0f455dba19d140de97725d',
  '7655942b7a91433391cdbfd44f790ba7',
  '358becee53944c448cf65b0a3b6be0fb',
  '47f2fc43e20b47d593a871c91248f150',
  '86733b9153144482bbc4fd9805e0bab0',
  'd0915e3527114cbfa9f8fd5f6d26fe0d',
  'b8d81831428b429badf100827b270809',
  '3bde328953cf40d6bd29104d56c8e537',
  'aece4294f55748dc815ac40e4016c26c',
  '33c6de0568224f9aa6fa5a1e758f3953',
  '03b88884f35d4c2ea091b029289014a8',
  'e8b804ebc29b47acbb9e47704d572464',
  '55e848ae01ae4bb790584efdceea95e3',
  '59e26c47436e4d58953b2bdcaf032190',
  'dca6a21a437449ce96c581cef89b43ab',
  '168db59116e04f09a4f846ddf93a536e',
  '41548aee208343ab9ecbc6e5f2a47c2f',
  '69704d8e18744fddbbe54e93cfd3894a',
  '2dae6d2085304bafaba703d7e1b2947a',
  'c445d7af0d024bbaa2ed312c4dd5e3d2'
];

// UUID에 하이픈 추가하는 함수
function formatUuid(uuid) {
  if (uuid.includes('-')) {
    return uuid; // 이미 하이픈이 있으면 그대로 반환
  }
  // 32자리 문자열을 8-4-4-4-12 형태로 변환
  return uuid.replace(/^(.{8})(.{4})(.{4})(.{4})(.{12})$/, '$1-$2-$3-$4-$5');
}

export default function () {
  // 랜덤 조직 UUID 선택 후 하이픈 추가
  const rawUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];
  const orgUuid = formatUuid(rawUuid);

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
    'response time < 2000ms': (r) => r.timings.duration < 2000,
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
