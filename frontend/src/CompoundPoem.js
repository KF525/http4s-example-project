export const CompoundPoem = ({poem: {title, firstLine, secondLine}}) =>
  <div>
    {title}
    <br/>
    {firstLine.line.text}
    <br/>
    {secondLine.line.text}
    <br/>
    by {firstLine.author.name} & {secondLine.author.name}
  </div>
