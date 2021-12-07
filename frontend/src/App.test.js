import { render, screen } from '@testing-library/react';
import Create from './Create';

test('renders learn react link', () => {
  render(<Create />);
  const linkElement = screen.getByText(/Loading.../i);
  expect(linkElement).toBeInTheDocument();
});
