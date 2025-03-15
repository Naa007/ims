    document.addEventListener("DOMContentLoaded", function () {
       console.log("Page loaded. JavaScript is working!");
    });

    //  **********************************   Inspector - Form JS - Start   **********************************
    // Function to add a certificate row
    function addCertificate(tableBodyId) {
     const tableBody = document.getElementById(tableBodyId);
     const rowCount = tableBody.children.length;
     const newRow = document.createElement("tr");
     newRow.innerHTML = `
         <td><input type="text" name="certificates[${rowCount}].name" class="form-control" placeholder="Certificate Name"/></td>
         <td><input type="date" name="certificates[${rowCount}].dateIssued" class="form-control"/></td>
         <td><input type="date" name="certificates[${rowCount}].expiryDate" class="form-control"/></td>
         <td><input type="text" name="certificates[${rowCount}].issuer" class="form-control" placeholder="Issuer"/></td>
         <td><button type="button" class="btn btn-danger" onclick="removeCertificateRow(this)"><i class="fas fa-trash"></i></button></td>
     `;
     tableBody.appendChild(newRow);
    }

     // Function to remove a certificate row
     function removeCertificateRow(button) {
         const row = button.closest("tr");
         row.remove();
     }

     // Form validation
     (function () {
         'use strict'

         // Fetch all forms we want to apply validation to
         var forms = document.querySelectorAll('.needs-validation')

         // Loop over them and prevent submission
         Array.prototype.slice.call(forms)
             .forEach(function (form) {
                 form.addEventListener('submit', function (event) {
                     if (!form.checkValidity()) {
                         event.preventDefault()
                         event.stopPropagation()
                     }

                     form.classList.add('was-validated')
                 }, false)
             })
     })()

     //  **********************************   Inspector - Form JS - End   **********************************

     //  **********************************   Inspector - Management JS - Start   **********************************

     function openInspectorForm() {
             window.location.href = '/inspectors/form';
         }

         function redirectToEditForm(inspectorId) {
             window.location.href = '/inspectors/edit/' + inspectorId;
         }

         function redirectToViewForm(inspectorId) {
             window.location.href = '/inspectors/view/' + inspectorId;
         }

         // Initialize DataTable with responsive extension
         $(document).ready(function () {
             var table = $('#inspectorTable').DataTable({
                 "paging": true,
                 "searching": true,
                 "ordering": true,
                 "info": true,
                 "lengthMenu": [5, 10, 25, 50],
                 "pageLength": 10,
                 "language": {
                     "search": "Search: ",
                     "lengthMenu": "Show _MENU_ entries",
                     "info": "Showing _START_ to _END_ of _TOTAL_ entries"
                 }
             });
         });

     //  **********************************   Inspector - Form JS - End   **********************************