#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script to generate all test case matrices (Matrix 8-20) for TEST_CASE_DESIGN_DOCUMENT.md
and export to Excel format
"""

import csv
import json
from typing import List, Dict, Any

# Matrix 8: createPurchaseRequestFromQuotation (UTCID94-UTCID105)
MATRIX_8 = {
    "matrix_number": 8,
    "function_code": "PR-001",
    "function_name": "createPurchaseRequestFromQuotation",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm createPurchaseRequestFromQuotation",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 7,
        "abnormal": 4,
        "boundary": 1,
        "total": 12
    },
    "utcids": ["UTCID94", "UTCID95", "UTCID96", "UTCID97", "UTCID98", "UTCID99", "UTCID100", "UTCID101", "UTCID102", "UTCID103", "UTCID104", "UTCID105"],
    "types": ["N", "N", "N", "N", "N", "N", "N", "A", "N", "A", "A", "B"]
}

# Matrix 9: createInvoice (UTCID106-UTCID117) - CHƯA IMPLEMENT
MATRIX_9 = {
    "matrix_number": 9,
    "function_code": "INV-001",
    "function_name": "createInvoice",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm createInvoice",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 7,
        "abnormal": 4,
        "boundary": 1,
        "total": 12
    },
    "utcids": ["UTCID106", "UTCID107", "UTCID108", "UTCID109", "UTCID110", "UTCID111", "UTCID112", "UTCID113", "UTCID114", "UTCID115", "UTCID116", "UTCID117"],
    "types": ["N", "N", "N", "N", "N", "N", "N", "A", "A", "A", "A", "B"]
}

# Matrix 10: updateTotalSpending (UTCID118-UTCID127)
MATRIX_10 = {
    "matrix_number": 10,
    "function_code": "CUST-001",
    "function_name": "updateTotalSpending",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm updateTotalSpending",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 10,
        "normal": 5,
        "abnormal": 3,
        "boundary": 2,
        "total": 10
    },
    "utcids": ["UTCID118", "UTCID119", "UTCID120", "UTCID121", "UTCID122", "UTCID123", "UTCID124", "UTCID125", "UTCID126", "UTCID127"],
    "types": ["N", "A", "B", "A", "N", "N", "N", "A", "B", "B"]
}

# Matrix 11: updateInventory (UTCID128-UTCID139)
MATRIX_11 = {
    "matrix_number": 11,
    "function_code": "PART-001",
    "function_name": "updateInventory",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm updateInventory",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 6,
        "abnormal": 4,
        "boundary": 2,
        "total": 12
    },
    "utcids": ["UTCID128", "UTCID129", "UTCID130", "UTCID131", "UTCID132", "UTCID133", "UTCID134", "UTCID135", "UTCID136", "UTCID137", "UTCID138", "UTCID139"],
    "types": ["N", "N", "A", "A", "B", "N", "N", "A", "N", "A", "B", "B"]
}

# Matrix 12: getDebtsByCustomer (UTCID140-UTCID151)
MATRIX_12 = {
    "matrix_number": 12,
    "function_code": "DE-002",
    "function_name": "getDebtsByCustomer",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm getDebtsByCustomer",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 9,
        "abnormal": 1,
        "boundary": 2,
        "total": 12
    },
    "utcids": ["UTCID140", "UTCID141", "UTCID142", "UTCID143", "UTCID144", "UTCID145", "UTCID146", "UTCID147", "UTCID148", "UTCID149", "UTCID150", "UTCID151"],
    "types": ["N", "N", "A", "N", "N", "N", "N", "N", "N", "N", "B", "B"]
}

# Matrix 13: rejectQuotationByCustomer (UTCID152-UTCID161)
MATRIX_13 = {
    "matrix_number": 13,
    "function_code": "PQ-004",
    "function_name": "rejectQuotationByCustomer",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm rejectQuotationByCustomer",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 10,
        "normal": 3,
        "abnormal": 6,
        "boundary": 1,
        "total": 10
    },
    "utcids": ["UTCID152", "UTCID153", "UTCID154", "UTCID155", "UTCID156", "UTCID157", "UTCID158", "UTCID159", "UTCID160", "UTCID161"],
    "types": ["N", "A", "A", "A", "A", "B", "N", "A", "A", "B"]
}

# Matrix 14: sendQuotationToCustomer (UTCID162-UTCID171)
MATRIX_14 = {
    "matrix_number": 14,
    "function_code": "PQ-005",
    "function_name": "sendQuotationToCustomer",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm sendQuotationToCustomer",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 10,
        "normal": 3,
        "abnormal": 6,
        "boundary": 1,
        "total": 10
    },
    "utcids": ["UTCID162", "UTCID163", "UTCID164", "UTCID165", "UTCID166", "UTCID167", "UTCID168", "UTCID169", "UTCID170", "UTCID171"],
    "types": ["N", "A", "A", "A", "A", "A", "N", "B", "A", "A"]
}

# Matrix 15: updateQuotationToDraft (UTCID172-UTCID181)
MATRIX_15 = {
    "matrix_number": 15,
    "function_code": "PQ-006",
    "function_name": "updateQuotationToDraft",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm updateQuotationToDraft",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 10,
        "normal": 4,
        "abnormal": 5,
        "boundary": 1,
        "total": 10
    },
    "utcids": ["UTCID172", "UTCID173", "UTCID174", "UTCID175", "UTCID176", "UTCID177", "UTCID178", "UTCID179", "UTCID180", "UTCID181"],
    "types": ["N", "A", "A", "A", "N", "A", "N", "B", "A", "N"]
}

# Matrix 16: createDebt (UTCID182-UTCID193)
MATRIX_16 = {
    "matrix_number": 16,
    "function_code": "DE-003",
    "function_name": "createDebt",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm createDebt",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 7,
        "abnormal": 3,
        "boundary": 2,
        "total": 12
    },
    "utcids": ["UTCID182", "UTCID183", "UTCID184", "UTCID185", "UTCID186", "UTCID187", "UTCID188", "UTCID189", "UTCID190", "UTCID191", "UTCID192", "UTCID193"],
    "types": ["N", "A", "A", "B", "B", "A", "N", "N", "N", "B", "B", "N"]
}

# Matrix 17: updateDueDate (UTCID194-UTCID203)
MATRIX_17 = {
    "matrix_number": 17,
    "function_code": "DE-004",
    "function_name": "updateDueDate",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm updateDueDate",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 10,
        "normal": 3,
        "abnormal": 5,
        "boundary": 2,
        "total": 10
    },
    "utcids": ["UTCID194", "UTCID195", "UTCID196", "UTCID197", "UTCID198", "UTCID199", "UTCID200", "UTCID201", "UTCID202", "UTCID203"],
    "types": ["N", "A", "A", "A", "A", "B", "N", "A", "B", "N"]
}

# Matrix 18: updateServiceTicket (UTCID204-UTCID215)
MATRIX_18 = {
    "matrix_number": 18,
    "function_code": "ST-002",
    "function_name": "updateServiceTicket",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm updateServiceTicket",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 6,
        "abnormal": 5,
        "boundary": 1,
        "total": 12
    },
    "utcids": ["UTCID204", "UTCID205", "UTCID206", "UTCID207", "UTCID208", "UTCID209", "UTCID210", "UTCID211", "UTCID212", "UTCID213", "UTCID214", "UTCID215"],
    "types": ["N", "A", "N", "A", "N", "N", "N", "A", "B", "N", "N", "N"]
}

# Matrix 19: processPaymentByPaymentLinkId (UTCID216-UTCID227)
MATRIX_19 = {
    "matrix_number": 19,
    "function_code": "TXN-002",
    "function_name": "processPaymentByPaymentLinkId",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm processPaymentByPaymentLinkId",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 12,
        "normal": 7,
        "abnormal": 4,
        "boundary": 0,
        "total": 12
    },
    "utcids": ["UTCID216", "UTCID217", "UTCID218", "UTCID219", "UTCID220", "UTCID221", "UTCID222", "UTCID223", "UTCID224", "UTCID225", "UTCID226", "UTCID227"],
    "types": ["N", "N", "A", "A", "N", "N", "N", "N", "N", "N", "A", "A"]
}

# Matrix 20: approvePurchaseRequest (UTCID228-UTCID237)
MATRIX_20 = {
    "matrix_number": 20,
    "function_code": "PR-002",
    "function_name": "approvePurchaseRequest",
    "created_by": "QA Team",
    "executed_by": "QA Team",
    "test_requirement": "Test đầy đủ các trường hợp của hàm approvePurchaseRequest",
    "summary": {
        "passed": 0,
        "failed": 0,
        "untested": 10,
        "normal": 4,
        "abnormal": 5,
        "boundary": 1,
        "total": 10
    },
    "utcids": ["UTCID228", "UTCID229", "UTCID230", "UTCID231", "UTCID232", "UTCID233", "UTCID234", "UTCID235", "UTCID236", "UTCID237"],
    "types": ["N", "A", "A", "A", "N", "A", "B", "A", "N", "A"]
}

ALL_MATRICES = [MATRIX_8, MATRIX_9, MATRIX_10, MATRIX_11, MATRIX_12, MATRIX_13, MATRIX_14, MATRIX_15, 
                MATRIX_16, MATRIX_17, MATRIX_18, MATRIX_19, MATRIX_20]

def generate_matrix_markdown(matrix_data: Dict[str, Any]) -> str:
    """Generate markdown table for a matrix"""
    lines = []
    lines.append(f"### MATRIX {matrix_data['matrix_number']}: {matrix_data['function_name']}")
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
    utcids = matrix_data['utcids']
    types = matrix_data['types']
    header = "| Condition Precondition | " + " | ".join([f"{utcid} [{t}]" for utcid, t in zip(utcids, types)]) + " |"
    lines.append(header)
    separator = "|" + "|".join(["---"] * (len(utcids) + 1)) + "|"
    lines.append(separator)
    
    # Basic preconditions row
    lines.append("| **Preconditions** |")
    lines.append("| Can connect with database | " + " | ".join(["O"] * len(utcids)) + " |")
    lines.append("")
    lines.append("---")
    lines.append("")
    
    return "\n".join(lines)

def export_to_csv(matrices: List[Dict[str, Any]], filename: str = "test_case_matrices.csv"):
    """Export matrices to CSV format"""
    with open(filename, 'w', newline='', encoding='utf-8-sig') as csvfile:
        writer = csv.writer(csvfile)
        
        # Write header
        writer.writerow([
            "Matrix", "Function Code", "Function Name", "UTCID", "Type", 
            "Description", "Status"
        ])
        
        # Write data
        for matrix_data in matrices:
            for utcid, tc_type in zip(matrix_data['utcids'], matrix_data['types']):
                writer.writerow([
                    f"Matrix {matrix_data['matrix_number']}",
                    matrix_data['function_code'],
                    matrix_data['function_name'],
                    utcid,
                    tc_type,
                    f"{matrix_data['function_name']} - {utcid}",
                    "Untested"
                ])

if __name__ == "__main__":
    # Generate markdown
    markdown_output = []
    for matrix in ALL_MATRICES:
        markdown_output.append(generate_matrix_markdown(matrix))
    
    # Write to file
    with open("matrices_8_20_output.md", "w", encoding="utf-8") as f:
        f.write("\n\n".join(markdown_output))
    
    # Export to CSV (can be opened in Excel)
    export_to_csv(ALL_MATRICES, "test_case_matrices_8_20.csv")
    
    print(f"Generated matrices_8_20_output.md and test_case_matrices_8_20.csv")
    print(f"Total matrices: {len(ALL_MATRICES)}")

