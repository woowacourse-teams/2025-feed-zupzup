function sum(a: number, b: number): number {
  return a + b;
}

describe('sum function', () => {
  it('adds two numbers correctly', () => {
    expect(sum(2, 3)).toBe(5);
  });

  it('works with negative numbers', () => {
    expect(sum(-1, -4)).toBe(-5);
  });
});
