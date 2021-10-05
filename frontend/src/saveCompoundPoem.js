export async function saveCompoundPoem(firstLine, secondLine, firstAuthor) {
  const requestOptions = {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      firstLine,
      secondLine,
      firstAuthor,
      secondAuthor: "tada"
    })
  };

  const response = await fetch('/compound', requestOptions)
  return await response.json()
}