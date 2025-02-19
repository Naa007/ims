   document.addEventListener("DOMContentLoaded", function () {
       console.log("Page loaded. JavaScript is working!");
   });

   function addCertificate() {
      // Get the certificates table body
      const certificatesBody = document.getElementById('certificatesBody');
      // Calculate the correct array index based on the current number of rows
      const currentIndex = certificatesBody.getElementsByTagName('tr').length;

      // Create a new row
      const newRow = document.createElement('tr');

      // Set the inner HTML for the new row with proper index for correct binding
      newRow.innerHTML = `
          <td><input type="text" name="certificates[${currentIndex}].name" class="form-control" placeholder="Enter certificate name"/></td>
          <td><input type="date" name="certificates[${currentIndex}].dateIssued" class="form-control"/></td>
          <td><input type="date" name="certificates[${currentIndex}].expiryDate" class="form-control"/></td>
          <td><input type="text" name="certificates[${currentIndex}].issuer" class="form-control" placeholder="Enter issuer"/></td>
          <td><button type="button" class="btn btn-danger" onclick="removeCertificateRow(this)">Remove</button></td>
      `;

      // Append the new row to the certificates table body
      certificatesBody.appendChild(newRow);
  }
   function removeCertificateRow(button) {
      // Remove the row containing the button
      const row = button.closest('tr');
      if (row) {
          row.remove();
      }
  }