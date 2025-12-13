#!/usr/bin/env python3
"""
Script to generate detailed test case matrices (similar to Matrix 1-5) 
for all implemented test cases based on existing test code.
"""

import re
import os
from pathlib import Path

def extract_test_info_from_file(file_path):
    """Extract test case information from a test file."""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    tests = []
    
    # Pattern to match test methods with UTCID
    pattern = r'@Test\s+void\s+(\w+UTCID\d+_\w+)\s*\([^)]*\)\s*\{[^}]*//\s*Given[^}]*//\s*When[^}]*//\s*Then[^}]*\}'
    
    # Find all test methods
    test_method_pattern = r'@Test\s+void\s+(UTCID\d+_\w+)\s*\([^)]*\)\s*\{'
    test_methods = re.finditer(test_method_pattern, content, re.MULTILINE | re.DOTALL)
    
    for match in test_methods:
        test_name = match.group(1)
        # Extract UTCID
        utcid_match = re.search(r'UTCID(\d+)', test_name)
        if utcid_match:
            utcid = f"UTCID{utcid_match.group(1)}"
            
            # Try to find JavaDoc comment before the test
            start_pos = match.start()
            # Look backwards for JavaDoc
            doc_pattern = r'/\*\*.*?\*/'
            doc_matches = list(re.finditer(doc_pattern, content[:start_pos], re.DOTALL))
            doc = ""
            if doc_matches:
                doc = doc_matches[-1].group(0)
            
            tests.append({
                'utcid': utcid,
                'name': test_name,
                'doc': doc
            })
    
    return tests

def generate_matrix_markdown(matrix_num, function_code, function_name, utcid_range, test_info_list):
    """Generate markdown for a test case matrix."""
    
    # Determine matrix details from existing matrices
    matrix_templates = {
        8: {
            'name': 'createPurchaseRequestFromQuotation',
            'code': 'PR-001',
            'utcids': list(range(94, 106)),
            'types': ['N', 'N', 'N', 'N', 'N', 'N', 'N', 'A', 'N', 'A', 'A', 'B']
        },
        10: {
            'name': 'updateTotalSpending',
            'code': 'CUST-001',
            'utcids': list(range(118, 128)),
            'types': ['N', 'A', 'B', 'A', 'N', 'N', 'N', 'A', 'B', 'B']
        },
        12: {
            'name': 'getDebtsByCustomer',
            'code': 'DE-002',
            'utcids': list(range(140, 152)),
            'types': ['N', 'N', 'A', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'B', 'B']
        }
    }
    
    if matrix_num not in matrix_templates:
        return ""
    
    template = matrix_templates[matrix_num]
    utcids = [f"UTCID{i}" for i in template['utcids']]
    types = template['types']
    
    # Build header
    markdown = f"### MATRIX {matrix_num}: {template['name']}\n\n"
    markdown += f"**Function Code:** {template['code']}\n"
    markdown += f"**Function Name:** {template['name']}\n"
    markdown += f"**Created By:** QA Team\n"
    markdown += f"**Executed By:** QA Team\n"
    markdown += f"**Test Requirement:** Test đầy đủ các trường hợp của hàm {template['name']}\n\n"
    
    # Build summary
    normal_count = types.count('N')
    abnormal_count = types.count('A')
    boundary_count = types.count('B')
    total = len(types)
    
    markdown += f"**SUMMARY:**\n"
    markdown += f"Passed: 0 | Failed: 0 | Untested: {total} | N: {normal_count} | A: {abnormal_count} | B: {boundary_count} | Total: {total}\n\n"
    
    # Build table header
    header = "| Condition Precondition | " + " | ".join([f"{utcid} [{t}]" for utcid, t in zip(utcids, types)]) + " |\n"
    separator = "|" + "|".join(["---"] * (len(utcids) + 1)) + "|\n"
    
    markdown += header
    markdown += separator
    
    # Add basic preconditions row
    markdown += "| **Preconditions** |\n"
    markdown += "| Can connect with database | " + " | ".join(["O"] * len(utcids)) + " |\n"
    
    # Add more rows based on test cases (simplified for now)
    # This would need to be expanded based on actual test implementations
    
    markdown += "\n---\n\n"
    
    return markdown

def main():
    """Main function to generate matrices."""
    
    # Find all test files
    test_dir = Path("src/test/java")
    test_files = list(test_dir.rglob("*Test.java"))
    
    # Generate matrices for known implementations
    matrices = []
    
    # Matrix 8: createPurchaseRequestFromQuotation
    pr_test_file = test_dir / "fpt/edu/vn/gms/service/impl/PurchaseRequestServiceImplTest.java"
    if pr_test_file.exists():
        tests = extract_test_info_from_file(pr_test_file)
        matrices.append((8, "PR-001", "createPurchaseRequestFromQuotation", tests))
    
    # Matrix 10: updateTotalSpending
    cust_test_file = test_dir / "fpt/edu/vn/gms/service/impl/CustomerServiceImplTest.java"
    if cust_test_file.exists():
        tests = extract_test_info_from_file(cust_test_file)
        matrices.append((10, "CUST-001", "updateTotalSpending", tests))
    
    # Matrix 12: getDebtsByCustomer
    debt_test_file = test_dir / "fpt/edu/vn/gms/service/impl/DebtServiceImplTest.java"
    if debt_test_file.exists():
        tests = extract_test_info_from_file(debt_test_file)
        matrices.append((12, "DE-002", "getDebtsByCustomer", tests))
    
    # Generate markdown
    output = ""
    for matrix_num, code, name, tests in matrices:
        output += generate_matrix_markdown(matrix_num, code, name, None, tests)
    
    # Write to file
    with open("detailed_matrices_output.md", "w", encoding="utf-8") as f:
        f.write(output)
    
    print(f"Generated {len(matrices)} detailed matrices")
    print("Output written to detailed_matrices_output.md")

if __name__ == "__main__":
    main()

