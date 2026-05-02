export default {
  testEnvironment: 'jsdom',
  transform: { '^.+\\.tsx?$': 'ts-jest' },
  setupFilesAfterEnv: ['@testing-library/jest-dom'],
  moduleNameMapper: { '\\.(css|less|scss)$': '<rootDir>/__mocks__/fileMock.js' },
}
