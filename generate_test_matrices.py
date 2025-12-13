#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script to generate test case matrices for TEST_CASE_DESIGN_DOCUMENT.md
and export to Excel format
"""

import csv
from typing import List, Dict, Any

# Matrix 6: createTransaction (UTCID70-UTCID81)
MATRIX_6 = {
    "function_code": "TXN-001",
    "function_name": "createTransaction",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm createTransaction",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 7,
        "abnormal": 4,
        "boundary": 1,
        "total": 12
    },
    "test_cases": [
        {
            "utcid": "UTCID70",
            "type": "N",
            "description": "Create cash transaction for invoice",
            "preconditions": {
                "can_connect_db": True,
                "invoice_exists": True,
                "payos_available": False
            },
            "inputs": {
                "invoice": "exists",
                "method": "CASH",
                "price": "500000L",
                "type": "PAYMENT"
            },
            "expected": "Successfully creates transaction with isActive=true"
        },
        {
            "utcid": "UTCID71",
            "type": "N",
            "description": "Create bank transfer with payment link",
            "preconditions": {
                "can_connect_db": True,
                "invoice_exists": True,
                "payos_available": True
            },
            "inputs": {
                "invoice": "exists",
                "method": "BANK_TRANSFER",
                "price": "500000L",
                "type": "PAYMENT"
            },
            "expected": "Payment link created, transaction with isActive=false"
        },
        {
            "utcid": "UTCID72",
            "type": "N",
            "description": "Create bank transfer for debt",
            "preconditions": {
                "can_connect_db": True,
                "debt_exists": True,
                "payos_available": True
            },
            "inputs": {
                "debt": "exists",
                "method": "BANK_TRANSFER",
                "type": "PAYMENT"
            },
            "expected": "Payment link with description contains 'Thanh toan cong no'"
        },
        {
            "utcid": "UTCID73",
            "type": "N",
            "description": "Return payment link when PayOS succeeds",
            "preconditions": {
                "can_connect_db": True,
                "payos_available": True,
                "payos_returns_link": True
            },
            "inputs": {
                "method": "BANK_TRANSFER"
            },
            "expected": "Response has paymentUrl from PayOS"
        },
        {
            "utcid": "UTCID74",
            "type": "A",
            "description": "PayOS API fails",
            "preconditions": {
                "can_connect_db": True,
                "payos_throws_exception": True
            },
            "inputs": {
                "method": "BANK_TRANSFER"
            },
            "expected": "Throws Exception, transaction not saved"
        },
        {
            "utcid": "UTCID75",
            "type": "N",
            "description": "Generate invoice description for deposit",
            "preconditions": {
                "can_connect_db": True,
                "invoice_exists": True,
                "type": "DEPOSIT"
            },
            "inputs": {
                "invoice": "exists",
                "type": "DEPOSIT"
            },
            "expected": "Description contains deposit information"
        },
        {
            "utcid": "UTCID76",
            "type": "N",
            "description": "Generate debt description for payment",
            "preconditions": {
                "can_connect_db": True,
                "debt_exists": True,
                "type": "PAYMENT"
            },
            "inputs": {
                "debt": "exists",
                "type": "PAYMENT"
            },
            "expected": "Description contains 'Thanh toan cong no'"
        },
        {
            "utcid": "UTCID77",
            "type": "N",
            "description": "Use default description",
            "preconditions": {
                "can_connect_db": True,
                "no_invoice_no_debt": True
            },
            "inputs": {
                "invoice": "null",
                "debt": "null"
            },
            "expected": "Default description used"
        },
        {
            "utcid": "UTCID78",
            "type": "N",
            "description": "Handle zero price",
            "preconditions": {
                "can_connect_db": True
            },
            "inputs": {
                "price": "0L"
            },
            "expected": "Transaction created with amount=0"
        },
        {
            "utcid": "UTCID79",
            "type": "A",
            "description": "Price is negative",
            "preconditions": {
                "can_connect_db": True
            },
            "inputs": {
                "price": "-100L"
            },
            "expected": "Throws ValidationException"
        },
        {
            "utcid": "UTCID80",
            "type": "N",
            "description": "Handle duplicate payment link",
            "preconditions": {
                "can_connect_db": True,
                "duplicate_payment_link": True
            },
            "inputs": {
                "paymentLinkId": "exists"
            },
            "expected": "Either creates new transaction or throws exception"
        },
        {
            "utcid": "UTCID81",
            "type": "A",
            "description": "Database save failure",
            "preconditions": {
                "can_connect_db": True,
                "save_throws_exception": True
            },
            "inputs": {
                "all_valid": True
            },
            "expected": "Throws DataAccessException"
        }
    ]
}

# Matrix 7: createExportFromQuotation (UTCID82-UTCID93)
MATRIX_7 = {
    "function_code": "SE-001",
    "function_name": "createExportFromQuotation",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm createExportFromQuotation",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 8,
        "abnormal": 3,
        "boundary": 1,
        "total": 12
    },
    "test_cases": [
        {
            "utcid": "UTCID82",
            "type": "N",
            "description": "Create export when valid quotation",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "export_not_exists": True,
                "code_service_available": True
            },
            "inputs": {
                "quotationId": "1L",
                "note": "Test reason",
                "createdBy": "employee"
            },
            "expected": "Successfully creates export with status=WAITING_TO_EXECUTE"
        },
        {
            "utcid": "UTCID83",
            "type": "A",
            "description": "Quotation not found",
            "preconditions": {
                "can_connect_db": True,
                "quotation_not_exists": True
            },
            "inputs": {
                "quotationId": "999L"
            },
            "expected": "Throws ResourceNotFoundException"
        },
        {
            "utcid": "UTCID84",
            "type": "A",
            "description": "Export already exists",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "export_exists": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Throws RuntimeException"
        },
        {
            "utcid": "UTCID85",
            "type": "N",
            "description": "Set EXPORTING status when part is available",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "part_available": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Item status = EXPORTING"
        },
        {
            "utcid": "UTCID86",
            "type": "N",
            "description": "Set WAITING_TO_RECEIPT when part is out of stock",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "part_out_of_stock": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Item status = WAITING_TO_RECEIPT"
        },
        {
            "utcid": "UTCID87",
            "type": "N",
            "description": "Set zero quantity exported",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "All items have quantityExported = 0.0"
        },
        {
            "utcid": "UTCID88",
            "type": "N",
            "description": "Generate export code",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "code_service_available": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Export code = 'XK-000001'"
        },
        {
            "utcid": "UTCID89",
            "type": "N",
            "description": "Assign creator",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "employee_exists": True
            },
            "inputs": {
                "quotationId": "1L",
                "createdBy": "employee"
            },
            "expected": "Export.createdBy = employee"
        },
        {
            "utcid": "UTCID90",
            "type": "N",
            "description": "Handle null creator",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "employee_null": True
            },
            "inputs": {
                "quotationId": "1L",
                "createdBy": "null"
            },
            "expected": "Export.createdBy = null"
        },
        {
            "utcid": "UTCID91",
            "type": "N",
            "description": "Handle multiple parts",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "multiple_parts": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Export has all items from quotation"
        },
        {
            "utcid": "UTCID92",
            "type": "A",
            "description": "Rollback on error",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "save_throws_exception": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Throws DataAccessException"
        },
        {
            "utcid": "UTCID93",
            "type": "B",
            "description": "Handle zero quantity",
            "preconditions": {
                "can_connect_db": True,
                "quotation_exists": True,
                "item_quantity_zero": True
            },
            "inputs": {
                "quotationId": "1L"
            },
            "expected": "Handles gracefully"
        }
    ]
}

def generate_matrix_table(matrix_data: Dict[str, Any]) -> str:
    """Generate markdown table for a matrix"""
    lines = []
    lines.append(f"### MATRIX {matrix_data.get('matrix_number', 'X')}: {matrix_data['function_name']}")
    lines.append("")
    lines.append(f"**Function Code:** {matrix_data['function_code']}")
    lines.append(f"**Function Name:** {matrix_data['function_name']}")
    lines.append(f"**Created By:** {matrix_data['created_by']}")
    lines.append(f"**Executed By:** {matrix_data['executed_by']}")
    lines.append(f"**Test Requirement:** {matrix_data['test_requirement']}")
    lines.append("")
    
    summary = matrix_data['summary']
    lines.append("**SUMMARY:**")
    lines.append(f"Passed: {summary['passed']} | Failed: {summary['failed']} | Untested: {summary['untested']} | "
                 f"N: {summary['normal']} | A: {summary['abnormal']} | B: {summary['boundary']} | Total: {summary['total']}")
    lines.append("")
    
    # Generate table header
    test_cases = matrix_data['test_cases']
    utcids = [tc['utcid'] for tc in test_cases]
    header = "| Condition Precondition | " + " | ".join([f"{utcid} [{tc['type']}]" for utcid, tc in zip(utcids, test_cases)]) + " |"
    lines.append(header)
    lines.append("|" + "|".join(["---"] * (len(utcids) + 1)) + "|")
    
    # This is a simplified version - full implementation would need to map all conditions
    lines.append("| **Preconditions** |")
    lines.append("| Can connect with database | " + " | ".join(["O"] * len(utcids)) + " |")
    
    lines.append("")
    lines.append("---")
    lines.append("")
    
    return "\n".join(lines)

def export_to_csv(matrices: List[Dict[str, Any]], filename: str = "test_case_matrices.csv"):
    """Export matrices to CSV format"""
    with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.writer(csvfile)
        
        # Write header
        writer.writerow([
            "Matrix", "Function Code", "Function Name", "UTCID", "Type", 
            "Description", "Preconditions", "Inputs", "Expected Result"
        ])
        
        # Write data
        for matrix_num, matrix_data in enumerate(matrices, start=6):
            for tc in matrix_data['test_cases']:
                writer.writerow([
                    f"Matrix {matrix_num}",
                    matrix_data['function_code'],
                    matrix_data['function_name'],
                    tc['utcid'],
                    tc['type'],
                    tc['description'],
                    str(tc.get('preconditions', {})),
                    str(tc.get('inputs', {})),
                    tc.get('expected', '')
                ])

if __name__ == "__main__":
    matrices = [MATRIX_6, MATRIX_7]
    
    # Generate markdown
    markdown_output = []
    for i, matrix in enumerate(matrices, start=6):
        matrix['matrix_number'] = i
        markdown_output.append(generate_matrix_table(matrix))
    
    # Write to file
    with open("matrices_output.md", "w", encoding="utf-8") as f:
        f.write("\n\n".join(markdown_output))
    
    # Export to CSV
    export_to_csv(matrices, "test_case_matrices.csv")
    
    print("Generated matrices_output.md and test_case_matrices.csv")

