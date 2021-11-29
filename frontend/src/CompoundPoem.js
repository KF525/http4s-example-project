export const CompoundPoem = ({poem: {firstLine, secondLine}}) =>
  <div className="CompoundPoem">
    <div className="prompt">{firstLine.line.text}</div>
    <div className="response">{secondLine.line.text}</div>
    <div className="author"> by {firstLine.author.name} & {secondLine.author.name}</div>
  </div>
