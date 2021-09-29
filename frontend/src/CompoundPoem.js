export const CompoundPoem = ({poem: {firstLine, secondLine}}) =>
  <div>
    {firstLine.line.text}
    <br/>
    {secondLine.line.text}
    <br/>
    by {firstLine.author.name} & {secondLine.author.name}
  </div>
