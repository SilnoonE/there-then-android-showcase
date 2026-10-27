// 실제 서비스 모델·파일 형식과 다르게 일반화한 짧은 예제입니다.
// 제품의 저장소, 사진 처리, ZIP, HTML 생성 전체는 포함하지 않습니다.
import assert from 'node:assert/strict';

function selectPublicFields(record, options = {}) {
  return {
    place: record.place,
    day: record.day,
    companions: options.includePeople ? [...record.companions] : [],
    note: options.includeNote ? record.note : '',
    area: options.includeArea ? record.area : '',
  };
}

function escapeText(value) {
  const replacements = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' };
  return String(value).replace(/[&<>"']/g, character => replacements[character]);
}

const fictional = {
  place: '가상의 작은 식탁',
  day: '2026-10-27',
  companions: ['동행 A'],
  note: '샘플 개인 메모',
  area: '예시 동네',
};
const original = structuredClone(fictional);
const basic = selectPublicFields(fictional);
assert.deepEqual(basic.companions, []);
assert.equal(basic.note, '');
assert.equal(basic.area, '');
assert.equal(basic.place, fictional.place);

const selected = selectPublicFields(fictional, { includePeople: true, includeNote: true });
assert.deepEqual(selected.companions, ['동행 A']);
assert.equal(selected.note, fictional.note);
assert.equal(selected.area, '');
selected.companions.push('동행 B');
assert.deepEqual(fictional, original);
assert.equal(escapeText('<예시> & "문장"'), '&lt;예시&gt; &amp; &quot;문장&quot;');

console.log('PASS: default privacy, selected fields, original preservation, and text escaping');
